package org.sensors2.osc.dispatch;

/**
 * Created by thomas on 11.11.14.
 */
public class SensorConfiguration {
	private boolean send;
	private int index;
	private int sensorType;
	private String oscParam;
	private float currentValue;
	private boolean sendDuplicates;

	public SensorConfiguration() {
	}

	public boolean sendingNeeded(float value) {
		if (!this.send) {
			return false;
		}
		if (sendDuplicates) {
			return true;
		}
		if (Math.abs(value - this.currentValue) == 0){
			return  false;
		}
		this.currentValue = value;
		return true;
	}

	public void setSend(boolean send) {
		this.send = send;
	}

	public void setSendDuplicates(boolean sendDuplicates) {
		this.sendDuplicates = sendDuplicates;
	}

	public int getIndex() {
		return this.index;
	}

	public int getSensorType() {
		return this.sensorType;
	}

	public String getOscParam() {
		return this.oscParam;
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
