package org.sensors2.osc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.sensors2.osc.R;
import org.sensors2.osc.activities.StartUpActivity;
import org.sensors2.osc.dispatch.Bundling;
import org.sensors2.osc.dispatch.SensorConfiguration;

import java.util.Map;

/**
 * Created by thomas on 09.11.14.
 */
public class SensorGroupFragment extends Fragment {

	private final SensorConfiguration sensorConfiguration;
	private CompoundButton activeButton;

	public SensorGroupFragment() {
		super();
		this.sensorConfiguration = new SensorConfiguration();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = this.getArguments();
		this.sensorConfiguration.setSensorType(args.getInt(Bundling.SENSOR_TYPE));
		this.sensorConfiguration.setOscParam(args.getString(Bundling.OSC_PREFIX));
		String name = args.getString(Bundling.NAME);

		//TODO: think about a better verification method
		if (sensorConfiguration.getOscParam().equals("nfc")) {
			this.sensorConfiguration.setSendDuplicates(true);
		}
		View v = inflater.inflate(R.layout.sensor_group, null);
		TextView groupName = (TextView) v.findViewById(R.id.group_name);
		groupName.setText(name);
		((TextView) v.findViewById(R.id.osc_prefix)).setText("/" + args.getString(Bundling.OSC_PREFIX));

		this.activeButton = (CompoundButton) v.findViewById(R.id.active);
		this.activeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
				sensorConfiguration.setSend(checked);
			}
		});
		return v;
	}

	public SensorConfiguration getSensorConfiguration() {
		return sensorConfiguration;
	}
}
