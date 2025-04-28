package org.sensors2.osc.bluetoothSensors.SensorHandlers;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import org.sensors2.osc.bluetoothSensors.SensorHandlers.models.BluetoothOscData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BarometricPressureHandler extends BaseSensorHandler implements SensorHandler {
    private static final UUID ENVIRONMENTAL_SENSING_SERVICE = new UUID(0x181A00001000L, 0x800000805f9b34fbL);
    public static final String OSC_ADDRESS = "barometricpressure";

    public static final ServiceMeasurementUUID BAROMETRIC_PRESSURE = new ServiceMeasurementUUID(
            ENVIRONMENTAL_SENSING_SERVICE,
            new UUID(0x2A6D00001000L, 0x800000805f9b34fbL)
    );

    @Override
    public List<ServiceMeasurementUUID> getServices() {
        return List.of(BAROMETRIC_PRESSURE);
    }

    @Override
    public BluetoothOscData getPayload(ServiceMeasurementUUID serviceMeasurementUUID, String sensorName, String address, BluetoothGattCharacteristic characteristic) {
        float value = parseEnvironmentalSensing(characteristic);
        if (value < 0) return null;
        return new BluetoothOscData(OSC_ADDRESS, Arrays.asList(new Float(value)));
    }


    /**
     * Decoding:
     * org.bluetooth.service.environmental_sensing.xml
     * org.bluetooth.characteristic.pressure.xml
     */
    public static float parseEnvironmentalSensing(BluetoothGattCharacteristic characteristic) {
        byte[] raw = characteristic.getValue();

        if (raw.length < 4) {
            return -1;
        }

        Integer pressure = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        return pressure / 1000f; // hPA
    }
}
