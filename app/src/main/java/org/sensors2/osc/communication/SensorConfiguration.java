package org.sensors2.osc.communication;

/**
 * Created by thomas on 11.11.14.
 */
public class SensorConfiguration {
	private boolean send;
	private int index;
	private int sensorType;
	private String oscParam;
	private float currentValue;
	private float sensitivity;

	public SensorConfiguration() {
	}

	public boolean sendingNeeded(float value) {
		if (!send) {
			return false;
		}
		if (Math.abs(value - this.currentValue) < sensitivity){
			return  false;
		}
		this.currentValue = value;
		return true;
	}

	public void setSend(boolean send) {
		this.send = send;
	}

	public int getIndex() {
		return index;
	}

	public int getSensorType() {
		return sensorType;
	}

	public String getOscParam() {
		return oscParam;
	}

	public void setSensitivity(float sensitivity) {
		this.sensitivity = sensitivity;
	}

	public void setOscParam(String oscParam) {
		this.oscParam = oscParam;
	}

	public void setSensorType(int sensorType) {
		this.sensorType = sensorType;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
