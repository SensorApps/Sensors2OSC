package org.sensors2.osc.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import org.sensors2.common.sensors.DataDispatcher;
import org.sensors2.common.sensors.Parameters;
import org.sensors2.common.sensors.SensorActivity;
import org.sensors2.common.sensors.SensorFactory;
import org.sensors2.osc.R;
import org.sensors2.osc.communication.OscConfiguration;
import org.sensors2.osc.communication.Sender;
import org.sensors2.osc.fragments.SensorFragment;
import org.sensors2.osc.fragments.SensorGroupFragment;
import org.sensors2.osc.sensors.Settings;

import java.util.ArrayList;
import java.util.List;


public class StartUpActivity extends FragmentActivity implements SensorEventListener, SensorActivity {

	private Settings settings;
	private SensorFactory sensorFactory;
	private Sender dispatcher;
	private SensorManager sensorManager;
	private CompoundButton activeButton;

	public Settings getSettings() {
		return this.settings;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.settings = this.loadSettings();
		this.dispatcher = new Sender();
		this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		this.sensorFactory = new SensorFactory(this);

		setContentView(R.layout.activity_start_up);
		this.activeButton = (CompoundButton) this.findViewById(R.id.active);
		for (Parameters parameters : this.sensorFactory.getSensors()) {
			this.CreateSensorFragments((org.sensors2.osc.sensors.Parameters) parameters);
		}
	}

	public List<Parameters> GetSensors(SensorManager sensorManager) {
		List<Parameters> parameters = new ArrayList<Parameters>();
		for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
			parameters.add(new org.sensors2.osc.sensors.Parameters(sensor.getType(), this.getApplicationContext()));
		}
		return parameters;
	}

	@Override
	public DataDispatcher getDispatcher() {
		return this.dispatcher;
	}

	@Override
	public SensorManager getSensorManager() {
		return this.sensorManager;
	}

	private Settings loadSettings() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Settings settings = new Settings(preferences);
		OscConfiguration oscConfiguration = OscConfiguration.getInstance();
		oscConfiguration.setHost(settings.getHost());
		oscConfiguration.setPort(settings.getPort());
		return settings;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_up, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.sensorFactory.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.loadSettings();
		this.dispatcher.setSensitivity(this.settings.getSensitivity());
		this.sensorFactory.onResume();
	}

	public void CreateSensorFragments(org.sensors2.osc.sensors.Parameters parameters) {
		FragmentManager manager = getSupportFragmentManager();
		SensorGroupFragment groupFragment = (SensorGroupFragment) manager.findFragmentByTag(parameters.getName());
		if (groupFragment == null) {
			this.CreateFragment(parameters, manager);
		}
	}

	public void CreateFragment(org.sensors2.osc.sensors.Parameters parameters, FragmentManager manager) {
		FragmentTransaction transaction = manager.beginTransaction();
		SensorGroupFragment groupFragment = new SensorGroupFragment();
		Bundle args = new Bundle();
		args.putInt("dimensions", parameters.getDimensions());
		args.putInt("sensorType", parameters.getSensorType());
		args.putString("oscPrefix", parameters.getOscPrefix());
		args.putString("name", parameters.getName());
		groupFragment.setArguments(args);
		transaction.add(R.id.sensor_group, groupFragment, parameters.getName());
		transaction.commit();
	}

	public void addSensorFragment(SensorFragment sensorFragment) {
		this.dispatcher.addSensorConfiguration(sensorFragment.getSensorConfiguration());
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		if (!activeButton.isChecked()) {
			return;
		}
		this.sensorFactory.dispatch(sensorEvent);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// We do not care about that
	}
}
