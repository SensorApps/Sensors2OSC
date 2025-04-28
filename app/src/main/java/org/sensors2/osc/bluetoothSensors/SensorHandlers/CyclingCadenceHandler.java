package org.sensors2.osc.bluetoothSensors.SensorHandlers;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import org.sensors2.osc.bluetoothSensors.SensorHandlers.models.BluetoothOscData;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class CyclingCadenceHandler extends BaseSensorHandler implements SensorHandler {
    private static final String TAG = CyclingPowerHandler.class.getSimpleName();

    @Override
    public List<ServiceMeasurementUUID> getServices() {
        return List.of(
                CyclingPowerHandler.CYCLING_POWER,
                CyclingDistanceSpeedHandler.CYCLING_SPEED_CADENCE);
    }

    @Override
    public BluetoothOscData getPayload(ServiceMeasurementUUID serviceMeasurementUUID, String sensorName, String address, BluetoothGattCharacteristic characteristic) {
        if (serviceMeasurementUUID.equals(CyclingPowerHandler.CYCLING_POWER)) {
            CyclingPowerHandler.Data data = CyclingPowerHandler.parseCyclingPower(characteristic);
            if (data != null && data.getCrank() != null) {
                return new BluetoothOscData(CyclingPowerHandler.OSC_ADDRESS, data.getCrank().asList());
            }
            return null;
        }

        if (serviceMeasurementUUID.equals(CyclingDistanceSpeedHandler.CYCLING_SPEED_CADENCE)) {
            Pair<CyclingDistanceSpeedHandler.WheelData, CrankData> data = CyclingDistanceSpeedHandler.parseCyclingCrankAndWheel(address, sensorName, characteristic);

            if (data != null && data.second != null) {
                return new BluetoothOscData(CyclingPowerHandler.OSC_ADDRESS, data.second.asList());
            }
            return null;
        }

        Log.e(TAG, "Don't know how to decode this payload.");
        return null;
    }

    public static class CrankData{
        private final long count; // UINT32
        private final int time; // UINT16; 1/1024s

        public CrankData(long count, int time) {
            this.count = count;
            this.time = time;
        }
        public List<Float> asList() {
            return Arrays.asList(new Float(count), new Float(time));
        }

        public float getCount() {
            return count;
        }

        public float getTime() {
            return time;
        }
    }
}
