package org.sensors2.osc.bluetoothSensors;

public interface DataChangedObserver {
    void onChange(String sensorName, Object data);
}
