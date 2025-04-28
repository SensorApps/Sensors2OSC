package org.sensors2.osc.bluetoothSensors.sensorHandlers;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import org.sensors2.osc.bluetoothSensors.sensorHandlers.models.BluetoothOscData;

import java.util.Collections;
import java.util.List;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class GeneralHandler extends  BaseSensorHandler implements  SensorHandler{
    @Override
    public List<ServiceMeasurementUUID> getServices() {
        return Collections.emptyList();
    }

    @Override
    public BluetoothOscData getPayload(ServiceMeasurementUUID serviceMeasurementUUID, String sensorName, String address, BluetoothGattCharacteristic characteristic) {
        return null;
    }
}
