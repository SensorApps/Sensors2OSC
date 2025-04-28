package org.sensors2.osc.bluetoothSensors.sensorHandlers;

import java.util.UUID;

public class ServiceMeasurementUUID {
    private final UUID serviceUUID;
    private final UUID measurementUUID;

    public ServiceMeasurementUUID(UUID serviceUUID, UUID measurementUUID){
        this.serviceUUID = serviceUUID;
        this.measurementUUID = measurementUUID;
    }

    public UUID getMeasurementUUID() {
        return measurementUUID;
    }

    public UUID getServiceUUID() {
        return serviceUUID;
    }
}
