package org.sensors2.osc.bluetoothSensors.SensorHandlers;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import org.sensors2.osc.bluetoothSensors.SensorHandlers.models.BluetoothOscData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class HeartRateHandler extends BaseSensorHandler implements SensorHandler {
    public static final String OSC_ADDRESS = "heartrate";

    public static final ServiceMeasurementUUID HEARTRATE = new ServiceMeasurementUUID(
            new UUID(0x180D00001000L, 0x800000805f9b34fbL),
            new UUID(0x2A3700001000L, 0x800000805f9b34fbL)
    );

    // Used for device discovery in preferences
    public static final List<ServiceMeasurementUUID> HEART_RATE_SUPPORTING_DEVICES = List.of(
            HEARTRATE,
            //Devices that support HEART_RATE_SERVICE_UUID, but do not announce HEART_RATE_SERVICE_UUID in there BLE announcement messages (during device discovery).
            new ServiceMeasurementUUID(
                    UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb"), //Miband3
                    HEARTRATE.getMeasurementUUID()
            )
    );

    @Override
    public List<ServiceMeasurementUUID> getServices() {
        return HEART_RATE_SUPPORTING_DEVICES;
    }

    @Override
    public BluetoothOscData getPayload(ServiceMeasurementUUID serviceMeasurementUUID, String sensorName, String address, BluetoothGattCharacteristic characteristic) {
        float heartRate = parseHeartRate(characteristic);
        if (heartRate == -1){
            return null;
        }
        return new BluetoothOscData(OSC_ADDRESS, Arrays.asList(heartRate));
    }

    private static float parseHeartRate(BluetoothGattCharacteristic characteristic) {
        //DOCUMENTATION https://www.bluetooth.com/wp-content/uploads/Sitecore-Media-Library/Gatt/Xml/Characteristics/org.bluetooth.characteristic.heart_rate_measurement.xml
        byte[] raw = characteristic.getValue();
        if (raw.length == 0) {
            return -1;
        }

        boolean formatUINT16 = ((raw[0] & 0x1) == 1);
        if (formatUINT16 && raw.length >= 3) {
            return characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
        }
        if (!formatUINT16 && raw.length >= 2) {
            return characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
        }

        return -1;
    }
}
