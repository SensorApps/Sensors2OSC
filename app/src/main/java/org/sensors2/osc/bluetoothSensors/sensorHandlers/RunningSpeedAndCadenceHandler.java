package org.sensors2.osc.bluetoothSensors.sensorHandlers;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import org.sensors2.osc.bluetoothSensors.sensorHandlers.models.BluetoothOscData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class RunningSpeedAndCadenceHandler extends BaseSensorHandler implements SensorHandler {
    private static final String OSC_ADDRESS = "runningspeedandcadence";

    public static final ServiceMeasurementUUID RUNNING_SPEED_CADENCE = new ServiceMeasurementUUID(
            new UUID(0x181400001000L, 0x800000805f9b34fbL),
            new UUID(0x2A5300001000L, 0x800000805f9b34fbL)
    );

    @Override
    public List<ServiceMeasurementUUID> getServices() {
        return List.of(RUNNING_SPEED_CADENCE);
    }

    @Override
    public BluetoothOscData getPayload(ServiceMeasurementUUID serviceMeasurementUUID, String sensorName, String address, BluetoothGattCharacteristic characteristic) {
        List<Float> payload = parseRunningSpeedAndCadence(sensorName, characteristic);
        if (payload == null) {
            return null;
        }
        return new BluetoothOscData(OSC_ADDRESS, payload);
    }

    public static List<Float> parseRunningSpeedAndCadence(String sensorName, @NonNull BluetoothGattCharacteristic characteristic) {
        // DOCUMENTATION https://www.bluetooth.com/wp-content/uploads/Sitecore-Media-Library/Gatt/Xml/Characteristics/org.bluetooth.characteristic.rsc_measurement.xml
        int valueLength = characteristic.getValue().length;
        if (valueLength == 0) {
            return null;
        }

        int flags = characteristic.getValue()[0];
        boolean hasStrideLength = (flags & 0x01) > 0;
        boolean hasTotalDistance = (flags & 0x02) > 0;
        boolean hasStatus = (flags & 0x03) > 0; // walking vs running

        float speed = -1;
        float cadence = -1;
        float totalDistance = -1;

        int index = 1;
        if (valueLength - index >= 2) {
            speed = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index) / 256f;
        }

        index = 3;
        if (valueLength - index >= 1) {
            cadence = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index);

            // Hacky workaround as the Wahoo Tickr X provides cadence in SPM (steps per minute) in violation to the standard.
            if (sensorName != null && sensorName.startsWith("TICKR X")) {
                cadence = cadence / 2;
            }
        }

        index = 4;
        if (hasStrideLength && valueLength - index >= 2) {
            float strideDistance = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
            index += 2;
        }

        if (hasTotalDistance && valueLength - index >= 4) {
            totalDistance = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, index);
        }

        return Arrays.asList(speed, cadence, totalDistance);
    }
}
