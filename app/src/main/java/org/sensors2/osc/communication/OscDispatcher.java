package org.sensors2.osc.communication;

import org.sensors2.common.sensors.DataDispatcher;
import org.sensors2.common.sensors.Measurement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 07.11.14.
 */
public class OscDispatcher implements DataDispatcher {

	private List<SensorConfiguration> sensorConfigurations;

	public OscDispatcher() {
		this.sensorConfigurations = new ArrayList<SensorConfiguration>();
	}

	public void addSensorConfiguration(SensorConfiguration sensorConfiguration) {
		this.sensorConfigurations.add(sensorConfiguration);
	}

	public void setSensitivity(float sensitivity){
		for(SensorConfiguration config : this.sensorConfigurations){
			config.setSensitivity(sensitivity);
		}
	}

	@Override
	public void dispatch(Measurement sensorData) {
		int length = sensorData.getValues().length;
		for (int i = 0; i < length; i++) {
			for (SensorConfiguration sensorConfiguration : this.sensorConfigurations) {
				if (sensorConfiguration.getIndex() == i && sensorConfiguration.getSensorType() == sensorData.getSensorType()) {
					this.trySend(sensorConfiguration, sensorData.getValues()[i]);
				}
			}
		}
	}

	private void trySend(SensorConfiguration sensorConfiguration, float value) {
		if (!sensorConfiguration.sendingNeeded(value)) {
			return;
		}
		new OscCommunication(OscConfiguration.getInstance()).execute(sensorConfiguration.getOscParam(), Float.toString(value));
	}
}
