package org.sensors2.osc.bluetoothSensors.sensorHandlers;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import org.sensors2.osc.bluetoothSensors.sensorHandlers.models.BluetoothOscData;

import java.util.List;

public interface SensorHandler {
    List<ServiceMeasurementUUID> getServices();
    BluetoothOscData getPayload(ServiceMeasurementUUID serviceMeasurementUUID, String sensorName, String address, BluetoothGattCharacteristic characteristic);
    void addGatt(BluetoothGatt gatt);
    void removeGatt(BluetoothGatt gatt);
    void disconnect();
}

