package org.sensors2.osc.dispatch;

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
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.os.HandlerCompat;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_NONE;
import static org.sensors2.osc.dispatch.SensorService.BT_PERMISSION_REQUEST;

class BluetoothConnections {

    private final Context context;

    public BluetoothConnections(Context context){
        this.context = context;
    }

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private final BluetoothGattCallback gattCallback = getGattCallback();
    private final Handler btHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));

    private BluetoothGattCallback getGattCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return new BluetoothGattCallback() {
                private Runnable discoverServicesRunnable;

                @Override
                public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyRead(gatt, txPhy, rxPhy, status);
                }

                @SuppressLint("MissingPermission")
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    if (status == BluetoothGatt.GATT_SUCCESS){
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            int bondstate = gatt.getDevice().getBondState();        // Take action depending on the bond state
                            if(bondstate == BOND_NONE || bondstate == BOND_BONDED) {
                                // Connected to device, now proceed to discover it's services but delay a bit if needed
                                int delayWhenBonded = 0;
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                                    delayWhenBonded = 1000;
                                }
                                final int delay = bondstate == BOND_BONDED ? delayWhenBonded : 0;
                                discoverServicesRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean result = gatt.discoverServices();
                                        discoverServicesRunnable = null;
                                    }
                                };
                                btHandler.postDelayed(discoverServicesRunnable, delay);
                            }
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                            gatt.close();
                        }
                    } else {
                        gatt.close();
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                }

                @Override
                public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
                    super.onCharacteristicRead(gatt, characteristic, value, status);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
                    super.onCharacteristicChanged(gatt, characteristic, value);
                }

                @Override
                public void onDescriptorRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor, int status, @NonNull byte[] value) {
                    super.onDescriptorRead(gatt, descriptor, status, value);
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                }

                @Override
                public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                    super.onReliableWriteCompleted(gatt, status);
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    super.onMtuChanged(gatt, mtu, status);
                }

                @Override
                public void onServiceChanged(@NonNull BluetoothGatt gatt) {
                    super.onServiceChanged(gatt);
                }
            };
        }
        return null;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void connect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
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
    }

    public void checkForPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothManager = activity.getSystemService(BluetoothManager.class);
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
}
