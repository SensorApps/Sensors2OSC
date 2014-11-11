package org.sensors2.osc.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.sensors2.osc.R;
import org.sensors2.osc.communication.SensorConfiguration;

public class SensorFragment extends Fragment {
	private CompoundButton activeButton;
	private SensorConfiguration sensorConfiguration;

	public SensorFragment(){
		super();
		this.sensorConfiguration = new SensorConfiguration();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.single_sensor, null);
		Bundle args = this.getArguments();
		this.sensorConfiguration.setIndex(args.getInt("index", 0));
		this.sensorConfiguration.setSensorType(args.getInt("sensorType"));
		this.sensorConfiguration.setOscParam(args.getString("oscPrefix"));
		String name = args.getString("name", null);

		if (name != null) {
			view.findViewById(R.id.name).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.name)).setText(name);
		}

		this.activeButton = (CompoundButton) view.findViewById(R.id.active);
		this.activeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
				sensorConfiguration.setSend(checked);
			}
		});
		return view;
	}

	public SensorConfiguration getSensorConfiguration() {
		return sensorConfiguration;
	}
}