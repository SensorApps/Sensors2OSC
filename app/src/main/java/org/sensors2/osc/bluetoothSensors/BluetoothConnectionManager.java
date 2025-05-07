package org.sensors2.osc.bluetoothSensors;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.sensors2.osc.bluetoothSensors.sensorHandlers.BarometricPressureHandler;
import org.sensors2.osc.bluetoothSensors.sensorHandlers.CyclingCadenceHandler;
import org.sensors2.osc.bluetoothSensors.sensorHandlers.CyclingDistanceSpeedHandler;
import org.sensors2.osc.bluetoothSensors.sensorHandlers.CyclingPowerHandler;
import org.sensors2.osc.bluetoothSensors.sensorHandlers.HeartRateHandler;
import org.sensors2.osc.bluetoothSensors.sensorHandlers.RunningSpeedAndCadenceHandler;
import org.sensors2.osc.bluetoothSensors.sensorHandlers.SensorHandler;
import org.sensors2.osc.bluetoothSensors.sensorHandlers.ServiceMeasurementUUID;
import org.sensors2.osc.bluetoothSensors.sensorHandlers.models.BluetoothOscData;
import org.sensors2.osc.dispatch.OscDispatcher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static org.sensors2.osc.dispatch.SensorService.BT_PERMISSION_REQUEST;

@SuppressLint("MissingPermission")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothConnectionManager {
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = new UUID(0x290200001000L, 0x800000805f9b34fbL);
    private static final String TAG = BluetoothConnectionManager.class.getSimpleName();
    private final Context context;
    private final List<SensorHandler> sensorHandlers = Arrays.asList(
            new BarometricPressureHandler(),
            new CyclingCadenceHandler(),
            new CyclingDistanceSpeedHandler(),
            new CyclingPowerHandler(),
            new HeartRateHandler(),
            new RunningSpeedAndCadenceHandler()
    );
    private final Map<UUID, SensorHandler> registeredHandlers = new HashMap<>();
    private OscDispatcher dispatcher;

    public BluetoothConnectionManager(Context context){
        this.context = context;
    }

    private BluetoothAdapter bluetoothAdapter;
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTING:
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    //This is also triggered, if no connection was established (ca. 30s)
                    gatt.connect();
                    break;
                //clearData();
            }
        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothGatt gatt, int status) {
            BluetoothGattService gattService = null;
            ServiceMeasurementUUID serviceMeasurement = null;
            for(SensorHandler sensorHandler : sensorHandlers){
                for (ServiceMeasurementUUID s : sensorHandler.getServices()) {
                    gattService = gatt.getService(s.getServiceUUID());
                    if (gattService != null) {
                        serviceMeasurement = s;
                        registeredHandlers.put(gattService.getUuid(), sensorHandler);
                        sensorHandler.addGatt(gatt);
                        break;
                    }
                }
                if (gattService != null) {
                    break;
                }
            }

            if (gattService == null) {
                Log.e(TAG, gatt.getDevice() + ": could not get gattService for serviceUUID=" + serviceMeasurement);
                return;
            }

            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(serviceMeasurement.getMeasurementUUID());
            if (characteristic == null) {
                Log.e(TAG, gatt.getDevice() + ": could not get BluetoothCharacteristic for serviceUUID=" + serviceMeasurement.getServiceUUID() + " characteristicUUID=" + serviceMeasurement.getMeasurementUUID());
                return;
            }
            gatt.setCharacteristicNotification(characteristic, true);

            // Register for updates.
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID);
            if (descriptor == null) {
                Log.e(TAG, "CLIENT_CHARACTERISTIC_CONFIG_UUID characteristic not available; cannot request notifications for changed data.");
                return;
            }

            if (!descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
                Log.e(TAG, "CLIENT_CHARACTERISTIC_CONFIG_UUID could not be set to ENABLE_NOTIFICATION_VALUE");
            }
            if (!gatt.writeDescriptor(descriptor)) {
                Log.e(TAG, "CLIENT_CHARACTERISTIC_CONFIG_UUID descriptor could not be written");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            UUID serviceUUID = characteristic.getService().getUuid();
            BluetoothDevice device = gatt.getDevice();
            Log.d(TAG, device + ": Received data with service " + serviceUUID + " and characteristics " + characteristic.getUuid());
            // get sensor handler
            SensorHandler sensorHandler = null;
            for(BluetoothGattService gattService : gatt.getServices()){
                sensorHandler = registeredHandlers.get(gattService.getUuid());
                if (sensorHandler != null){
                    break;
                }
            }
            if (sensorHandler == null){
                return;
            }
            ServiceMeasurementUUID serviceMeasurement = null;

            for (ServiceMeasurementUUID s : sensorHandler.getServices()) {
                BluetoothGattService gattService = gatt.getService(s.getServiceUUID());
                if (gattService != null) {
                    serviceMeasurement = s;
                    break;
                }
            }

            if (serviceMeasurement == null) {
                Log.e(TAG, device + ": Unknown service UUID; not supported?");
                return;
            }

            BluetoothOscData data = sensorHandler.getPayload(serviceMeasurement, gatt.getDevice().getName(), gatt.getDevice().getAddress(), characteristic);
            if (dispatcher != null && data != null) {
                dispatcher.dispatch(data);
            }
        }
    };

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void connect() {
        if (bluetoothAdapter == null){
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getBondState() == BOND_BONDED) {
                    BluetoothClass bluetoothClass = device.getBluetoothClass();
                    switch(bluetoothClass.getMajorDeviceClass()) {
                        case BluetoothClass.Device.Major.HEALTH:
                        case BluetoothClass.Device.Major.MISC:
                        case BluetoothClass.Device.Major.PERIPHERAL:
                        case BluetoothClass.Device.Major.TOY:
                        case BluetoothClass.Device.Major.UNCATEGORIZED:
                            device.connectGatt(context, true, gattCallback);
                            String deviceName = device.getName();
                            Log.d(deviceName, bluetoothClass.getMajorDeviceClass() + "; " + bluetoothClass.getDeviceClass());
                            break;
                    }
                }
            }
        }
    }

    public void disconnect() {
        for(SensorHandler handler : sensorHandlers){
            handler.disconnect();
        }
    }

    public void checkForPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            BluetoothManager bluetoothManager = activity.getSystemService(BluetoothManager.class);
            bluetoothAdapter = bluetoothManager.getAdapter();

            if (bluetoothAdapter != null) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                } else {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BT_PERMISSION_REQUEST);
                    }
                }
            }
        }
    }

    public void setDispatcher(OscDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
