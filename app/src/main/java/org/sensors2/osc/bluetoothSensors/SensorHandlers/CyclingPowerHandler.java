package org.sensors2.osc.bluetoothSensors.SensorHandlers;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import org.sensors2.osc.bluetoothSensors.SensorHandlers.models.BluetoothOscData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class CyclingPowerHandler implements SensorHandler{

    public static final String OSC_ADDRESS = "cyclingpower";

    public static final ServiceMeasurementUUID CYCLING_POWER = new ServiceMeasurementUUID(
            new UUID(0x181800001000L, 0x800000805f9b34fbL),
            new UUID(0x2A6300001000L, 0x800000805f9b34fbL)
    );

    @Override
    public List<ServiceMeasurementUUID> getServices() {
        return List.of(CYCLING_POWER);
    }

    @Override
    public BluetoothOscData getPayload(ServiceMeasurementUUID serviceMeasurementUUID, String sensorName, String address, BluetoothGattCharacteristic characteristic) {
        Data cyclingPower = parseCyclingPower(characteristic);

        if (cyclingPower != null) {
            return new BluetoothOscData(OSC_ADDRESS, cyclingPower.asList());
        }
        return null;
    }
    public static Data parseCyclingPower(BluetoothGattCharacteristic characteristic) {
        // DOCUMENTATION https://www.bluetooth.com/wp-content/uploads/Sitecore-Media-Library/Gatt/Xml/Characteristics/org.bluetooth.characteristic.cycling_power_measurement.xml
        int valueLength = characteristic.getValue().length;
        if (valueLength == 0) {
            return null;
        }

        int index = 0;
        int flags1 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index++);
        int flags2 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index++);
        boolean hasPedalPowerBalance = (flags1 & 0x01) > 0;
        boolean hasAccumulatedTorque = (flags1 & 0x04) > 0;
        boolean hasWheel = (flags1 & 16) > 0;
        boolean hasCrank = (flags1 & 32) > 0;

        Integer instantaneousPower = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, index);
        index += 2;

        if (hasPedalPowerBalance) {
            index += 1;
        }
        if (hasAccumulatedTorque) {
            index += 2;
        }
        if (hasWheel) {
            index += 2 + 2;
        }

        CyclingCadenceHandler.CrankData crankData = null;
        if (hasCrank && valueLength - index >= 4) {
            long crankCount = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
            index += 2;

            int crankTime = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index); // 1/1024s

            crankData = new CyclingCadenceHandler.CrankData(crankCount, crankTime);
        }

        return new Data(instantaneousPower, crankData);
    }

    public static class Data{
        private final CyclingCadenceHandler.CrankData crank;
        private final float power;

        public Data(float power, CyclingCadenceHandler.CrankData crank) {
            this.power = power;
            this.crank = crank;
        }

        public List<Float> asList() {
            return Arrays.asList(new Float(power), new Float(crank.getCount()), new Float(crank.getTime()));
        }

        public CyclingCadenceHandler.CrankData getCrank() {
            return crank;
        }
    }

}
