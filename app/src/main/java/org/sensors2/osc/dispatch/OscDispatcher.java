package org.sensors2.osc.dispatch;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;

import org.sensors2.common.dispatch.Measurement;
import org.sensors2.common.dispatch.DataDispatcher;
import org.sensors2.osc.sensors.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 07.11.14.
 */
public class OscDispatcher implements DataDispatcher {
	private List<SensorConfiguration> sensorConfigurations = new ArrayList<SensorConfiguration>();
	private OscCommunication communication;
	private float[] gravity;
	private float[] geomagnetic;
	private SensorManager sensorManager;

	public OscDispatcher() {
		communication = new OscCommunication("OSC dispatcher thread", Thread.MIN_PRIORITY);
		communication.start();
	}

	public void addSensorConfiguration(SensorConfiguration sensorConfiguration) {
		this.sensorConfigurations.add(sensorConfiguration);
	}

	@Override
	public void dispatch(Measurement sensorData) {
		for (SensorConfiguration sensorConfiguration : this.sensorConfigurations) {
			if (sensorConfiguration.getSensorType() == sensorData.getSensorType()) {
				trySend(sensorConfiguration, sensorData.getValues());
			}
			if (sensorConfiguration.getSensorType() == Parameters.FAKE_ORIENTATION){
				// Fake orientation
				if (sensorData.getSensorType() != Sensor.TYPE_ACCELEROMETER && sensorData.getSensorType() != Sensor.TYPE_MAGNETIC_FIELD){
					continue;
				}
				if (sensorData.getSensorType() == Sensor.TYPE_ACCELEROMETER){
					this.gravity = sensorData.getValues();
				}

				if (sensorData.getSensorType() == Sensor.TYPE_MAGNETIC_FIELD){
					this.geomagnetic = sensorData.getValues();
				}
				if (this.gravity != null && this.geomagnetic != null){
					float rotation[] = new float[9];
					float inclination[] = new float[9];

					boolean success = this.sensorManager.getRotationMatrix(rotation, inclination, this.gravity, this.geomagnetic);
					if (success) {
						float orientation[] = new float[3];
						this.sensorManager.getOrientation(rotation, orientation);
						this.trySend(sensorConfiguration, orientation);
					}
				}
			}
		}
	}

	private void trySend(SensorConfiguration sensorConfiguration, float[] values) {
		if (!sensorConfiguration.sendingNeeded(values)) {
			return;
		}
		Message message = new Message();
		Bundle data = new Bundle();
		data.putFloatArray(Bundling.VALUE, values);
		data.putString(Bundling.OSC_PARAMETER, sensorConfiguration.getOscParam());
		message.setData(data);
		OscHandler handler = communication.getOscHandler();
		handler.sendMessage(message);
	}

	public void setSensorManager(SensorManager sensorManager) {
		this.sensorManager = sensorManager;
	}
}
