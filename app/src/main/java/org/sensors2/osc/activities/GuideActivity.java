package org.sensors2.osc.activities;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

import org.sensors2.osc.R;
import org.sensors2.osc.dispatch.Bundling;
import org.sensors2.osc.fragments.HelpSensorGroupFragment;
import org.sensors2.osc.sensors.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 12.02.15.
 */
public class GuideActivity extends FragmentActivity {
	private SensorManager sensorManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		TextView availableSensorsHeadline = (TextView) findViewById(R.id.availSensorsHeadline);
		this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Parameters> sensors = GetSensors(sensorManager);
		availableSensorsHeadline.setText(sensors.size() + " " + availableSensorsHeadline.getText());
		for (Parameters parameters : sensors) {
			this.CreateSensorFragments(parameters);
		}
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	public void CreateSensorFragments(Parameters parameters) {
		FragmentManager manager = getSupportFragmentManager();
		HelpSensorGroupFragment groupFragment = (HelpSensorGroupFragment) manager.findFragmentByTag(parameters.getName());
		if (groupFragment == null) {
			this.CreateFragment(parameters, manager);
		}
	}

	public void CreateFragment(Parameters parameters, FragmentManager manager) {
		FragmentTransaction transaction = manager.beginTransaction();
		HelpSensorGroupFragment groupFragment = new HelpSensorGroupFragment();
		Bundle args = new Bundle();
		args.putInt(Bundling.DIMENSIONS, parameters.getDimensions());
		args.putInt(Bundling.SENSOR_TYPE, parameters.getSensorType());
		args.putString(Bundling.OSC_PREFIX, parameters.getOscPrefix());
		args.putString(Bundling.NAME, parameters.getName());
		args.putString(Bundling.SENSOR_NAME, parameters.getSensorName());
		args.putFloat(Bundling.SENSOR_RANGE, parameters.getRange());
		args.putFloat(Bundling.RESOLUTION, parameters.getResolution());
		groupFragment.setArguments(args);
		transaction.add(R.id.sensor_group, groupFragment, parameters.getName());
		transaction.commit();
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
