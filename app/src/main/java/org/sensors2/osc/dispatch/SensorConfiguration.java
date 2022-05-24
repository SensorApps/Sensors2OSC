package org.sensors2.osc.dispatch;

import org.sensors2.osc.sensors.Parameters;

/**
 * Created by thomas on 11.11.14.
 */
public class SensorConfiguration {
    private final float[] currentValues = new float[Parameters.MAX_DIMENSIONS];
    private boolean send;
    private int sensorType;
    private String oscParam;
    private boolean sendDuplicates;

    public SensorConfiguration() {
    }

    public boolean sendingNotNeeded(float[] values) {
        if (!this.send) {
            return true;
        }
        if (sendDuplicates) {
            return false;
        }
        boolean differenceDetected = false;
        for (int i = 0; i < values.length && i < Parameters.MAX_DIMENSIONS; i++) {
            if (Math.abs(values[i] - this.currentValues[i]) != 0) {
                differenceDetected = true;
            }
            this.currentValues[i] = values[i];
        }
        return !differenceDetected;
    }

    public boolean getSend() {
        return this.send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public void setSendDuplicates(boolean sendDuplicates) {
        this.sendDuplicates = sendDuplicates;
    }

    public int getSensorType() {
        return this.sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public String getOscParam() {
        return this.oscParam;
    }

    public void setOscParam(String oscParam) {
        this.oscParam = oscParam;
    }
}
