package org.sensors2.osc.bluetoothSensors.SensorHandlers;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Pair;

import org.sensors2.osc.bluetoothSensors.SensorHandlers.models.BluetoothOscData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class CyclingDistanceSpeedHandler extends BaseSensorHandler implements SensorHandler {
    public static final ServiceMeasurementUUID CYCLING_SPEED_CADENCE = new ServiceMeasurementUUID(
            new UUID(0x181600001000L, 0x800000805f9b34fbL),
            new UUID(0x2A5B00001000L, 0x800000805f9b34fbL)
    );
    private static final String OSC_ADDRESS = "cyclingdistance" ;

    @Override
    public List<ServiceMeasurementUUID> getServices() {
        return List.of(CYCLING_SPEED_CADENCE);
    }


    @Override
    public BluetoothOscData getPayload(ServiceMeasurementUUID serviceMeasurementUUID, String sensorName, String address, BluetoothGattCharacteristic characteristic) {
        Pair<WheelData, CyclingCadenceHandler.CrankData> data = parseCyclingCrankAndWheel(address, sensorName, characteristic);
        if (data.first != null) {
            return new BluetoothOscData(OSC_ADDRESS, data.first.asList());
        }
        return null;
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public static Pair<WheelData, CyclingCadenceHandler.CrankData> parseCyclingCrankAndWheel(String address, String sensorName, @NonNull BluetoothGattCharacteristic characteristic) {
        // DOCUMENTATION https://www.bluetooth.com/wp-content/uploads/Sitecore-Media-Library/Gatt/Xml/Characteristics/org.bluetooth.characteristic.csc_measurement.xml
        int valueLength = characteristic.getValue().length;
        if (valueLength == 0) {
            return null;
        }

        int flags = characteristic.getValue()[0];
        boolean hasWheel = (flags & 0x01) > 0;
        boolean hasCrank = (flags & 0x02) > 0;

        int index = 1;
        WheelData wheelData = null;
        if (hasWheel && valueLength - index >= 6) {
            long wheelTotalRevolutionCount = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, index);
            index += 4;
            int wheelTime = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index); // 1/1024s
            wheelData = new WheelData(wheelTotalRevolutionCount, wheelTime);
            index += 2;
        }

        CyclingCadenceHandler.CrankData crankData = null;
        if (hasCrank && valueLength - index >= 4) {
            long crankCount = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
            index += 2;

            int crankTime = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index); // 1/1024s
            crankData = new CyclingCadenceHandler.CrankData(crankCount, crankTime);
        }

        return new Pair<>(wheelData, crankData);
    }

    public static class WheelData{
        private final long wheelRevolutionsCount; // UINT32
        private final int wheelRevolutionsTime; // UINT16; 1/1024s

        public WheelData(long wheelRevolutionsCount, int wheelRevolutionsTime){
            this.wheelRevolutionsCount = wheelRevolutionsCount;
            this.wheelRevolutionsTime = wheelRevolutionsTime;
        }

        public long getWheelRevolutionsCount() {
            return wheelRevolutionsCount;
        }

        public int getWheelRevolutionsTime() {
            return wheelRevolutionsTime;
        }

        public List<Float> asList() {
            return Arrays.asList(new Float(wheelRevolutionsCount), new Float(wheelRevolutionsTime));
        }
    }
}
