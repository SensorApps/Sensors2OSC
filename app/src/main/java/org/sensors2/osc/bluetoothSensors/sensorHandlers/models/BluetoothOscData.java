package org.sensors2.osc.bluetoothSensors.sensorHandlers.models;

import java.util.List;

public class BluetoothOscData {
    private final String oscAddress;
    private final float[] data;

    public BluetoothOscData(String oscAddress, List<Float> data) {
        this.oscAddress = oscAddress;
        float[] floatArray = new float[data.size()];
        int i = 0;
        for (Float f : data) {
            floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        this.data = floatArray;
    }

    public float[] getData() {
        return data;
    }

    public String getOscAddress() {
        return oscAddress;
    }
}
