package org.sensors2.osc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sensors2.osc.R;
import org.sensors2.osc.dispatch.Bundling;
import org.sensors2.osc.sensors.SensorDimensions;

import java.util.Map;

/**
 * Created by thomas on 09.11.14.
 */
public class HelpSensorGroupFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = this.getArguments();
		int dimensions = args.getInt(Bundling.DIMENSIONS);
		int sensorType = args.getInt(Bundling.SENSOR_TYPE);
		String oscPrefix = args.getString(Bundling.OSC_PREFIX);

		String name = args.getString(Bundling.NAME);
		String sensorName = args.getString(Bundling.SENSOR_NAME);
		View v = inflater.inflate(R.layout.help_sensor_group, null);
		TextView groupName = (TextView) v.findViewById(R.id.group_name);
		groupName.setText(name + " (" + sensorName + ")");
		AddText((TextView) v.findViewById(R.id.range), args.getFloat(Bundling.SENSOR_RANGE));
		AddText((TextView) v.findViewById(R.id.resolution), args.getFloat(Bundling.RESOLUTION));
		CreateSensors(sensorType, oscPrefix, dimensions);
		return v;
	}

	private void AddText(TextView view, float val) {
		view.setText(view.getText() + ": " + val);
	}

	private void CreateSensors(int sensorType, String oscPrefix, int dimensions) {
		for (Map.Entry<Integer, String> oscSuffix : SensorDimensions.GetOscSuffixes(dimensions).entrySet()) {
			FragmentManager manager = getChildFragmentManager();
			String fragmentTag = oscPrefix + oscSuffix.getValue();
			HelpSensorFragment sensorFragment = (HelpSensorFragment) manager.findFragmentByTag(fragmentTag);
			if (sensorFragment == null) {
				CreateSensorFragment(manager, fragmentTag);
			}
		}
	}

	private void CreateSensorFragment(FragmentManager manager, String fragmentTag) {
		FragmentTransaction transaction = manager.beginTransaction();
		HelpSensorFragment sensorFragment = new HelpSensorFragment();
		Bundle args = new Bundle();
		args.putString(Bundling.OSC_PREFIX, fragmentTag);
		sensorFragment.setArguments(args);
		transaction.add(R.id.sensor_list, sensorFragment, fragmentTag);
		transaction.commit();
	}
}
