package org.sensors2.osc.activities;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

import org.sensors2.osc.R;
import org.sensors2.osc.sensors.Parameters;
import org.sensors2.osc.sensors.SensorDimensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by thomas on 12.02.15.
 */
public class GuideActivity extends Activity {
	private SensorManager sensorManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		TextView availableSensorsHeadline = (TextView) findViewById(R.id.availSensorsHeadline);
		TextView textViewAvailableSensors = (TextView) findViewById(R.id.textViewAvailableSensors);
		this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		StringBuilder availableSensors = new StringBuilder();
		List<Parameters> sensors = GetSensors(sensorManager);
		availableSensorsHeadline.setText(sensors.size() + " " + availableSensorsHeadline.getText());
		for (Parameters parameters : sensors) {
			availableSensors.append("\n" + parameters.getName() + " (" + parameters.getSensorName() + ")" +
					"\n max range: " + parameters.getRange() +
					"\n resolution: " + parameters.getResolution() +
					"\n send: ");
			availableSensors.append(GetSensorMappings(parameters));

			availableSensors.append("\n");
		}
		textViewAvailableSensors.setText(availableSensors);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	private StringBuilder GetSensorMappings(Parameters parameters) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<Integer, String> oscSuffix : SensorDimensions.GetOscSuffixes(parameters.getDimensions()).entrySet()) {
			builder.append(parameters.getOscPrefix() + oscSuffix.getValue() + " ");
		}
		return builder;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public List<Parameters> GetSensors(SensorManager manager) {
		List<Parameters> parameters = new ArrayList<Parameters>();
		for (Sensor sensor : manager.getSensorList(Sensor.TYPE_ALL)) {
			parameters.add(new Parameters(sensor, this));
		}
		return parameters;
	}
}
