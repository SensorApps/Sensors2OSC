package org.sensors2.osc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.sensors2.osc.R;
import org.sensors2.osc.activities.StartUpActivity;
import org.sensors2.osc.dispatch.Bundling;
import org.sensors2.osc.dispatch.SensorConfiguration;
import org.sensors2.osc.dispatch.SensorService;

import androidx.fragment.app.Fragment;

/**
 * Created by thomas on 09.11.14.
 */
public class SensorFragment extends Fragment {

	private final SensorConfiguration sensorConfiguration;
	private CompoundButton activeButton;
	private SensorService sensorService;

	public void setSensorService(SensorService sensorService){
		this.sensorService = sensorService;
		this.activeButton.setChecked( this.sensorService.getSensorActivation(this.sensorConfiguration.getSensorType()) );
	}

	public SensorFragment() {
		super();
		this.sensorConfiguration = new SensorConfiguration();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = this.getArguments();
		this.sensorConfiguration.setSensorType(args.getInt(Bundling.SENSOR_TYPE));
		String name = args.getString(Bundling.NAME);

		View v = inflater.inflate(R.layout.sensor, null);
		TextView groupName = (TextView) v.findViewById(R.id.group_name);
		groupName.setText(name);
		((TextView) v.findViewById(R.id.osc_prefix)).setText("/" + args.getString(Bundling.OSC_PREFIX));
		StartUpActivity activity = (StartUpActivity) getActivity();
		activity.registerFragment(this);

		this.activeButton = (CompoundButton) v.findViewById(R.id.active);
		this.activeButton.setOnCheckedChangeListener((compoundButton, checked) -> SensorFragment.this.sensorService.setSensorActivation(SensorFragment.this.sensorConfiguration.getSensorType(), checked));
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
