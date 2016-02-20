package org.sensors2.osc.dispatch;

import android.os.Bundle;
import android.os.Message;

import org.sensors2.common.dispatch.Measurement;
import org.sensors2.common.dispatch.DataDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 07.11.14.
 */
public class OscDispatcher implements DataDispatcher {
	private List<SensorConfiguration> sensorConfigurations = new ArrayList<SensorConfiguration>();
	private OscCommunication communication;

	public OscDispatcher() {
		communication = new OscCommunication("OSC dispatcher thread", Thread.MIN_PRIORITY);
		communication.start();
	}

	public void addSensorConfiguration(SensorConfiguration sensorConfiguration) {
		this.sensorConfigurations.add(sensorConfiguration);
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
		Message message = new Message();
		Bundle data = new Bundle();
		data.putFloat(Bundling.VALUE, value);
		data.putString(Bundling.OSC_PARAMETER, sensorConfiguration.getOscParam());
		message.setData(data);
		OscHandler handler = communication.getOscHandler();
		handler.sendMessage(message);
	}
}
