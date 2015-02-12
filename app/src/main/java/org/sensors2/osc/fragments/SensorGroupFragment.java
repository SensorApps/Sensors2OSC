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
import org.sensors2.osc.activities.StartUpActivity;
import org.sensors2.osc.sensors.SensorDimensions;

import java.util.Map;

/**
 * Created by thomas on 09.11.14.
 */
public class SensorGroupFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle args = this.getArguments();
		int dimensions = args.getInt("dimensions");
		int sensorType = args.getInt("sensorType");
		String oscPrefix = args.getString("oscPrefix");

		String name = args.getString("name");
		View v = inflater.inflate(R.layout.sensor_group, null);
		TextView groupName = (TextView) v.findViewById(R.id.group_name);
		groupName.setText(name);
		((TextView) v.findViewById(R.id.osc_prefix)).setText("/" + args.getString("oscPrefix"));
		CreateSensors(sensorType, oscPrefix, dimensions);
		return v;
	}

	private void CreateSensors(int sensorType, String oscPrefix, int dimensions) {
		for (Map.Entry<Integer, String> oscSuffix : SensorDimensions.GetOscSuffixes(dimensions).entrySet()) {
			FragmentManager manager = getChildFragmentManager();
			String fragmentTag = oscPrefix + oscSuffix.getValue();
			SensorFragment sensorFragment = (SensorFragment) manager.findFragmentByTag(fragmentTag);
			if (sensorFragment == null) {
				sensorFragment = CreateSensorFragment(sensorType, oscPrefix, oscSuffix.getKey(), oscSuffix.getValue(), manager, fragmentTag);
			}
			StartUpActivity activity = (StartUpActivity) this.getActivity();
			activity.addSensorFragment(sensorFragment);
		}
	}


	private SensorFragment CreateSensorFragment(int sensorType, String oscPrefix, int i, String direction, FragmentManager manager, String fragmentTag) {
		SensorFragment sensorFragment;
		FragmentTransaction transaction = manager.beginTransaction();
		sensorFragment = new SensorFragment();
		Bundle args = new Bundle();
		args.putInt("sensorType", sensorType);
		args.putString("name", direction);
		args.putString("oscPrefix", oscPrefix + direction);
		args.putInt("index", i);
		sensorFragment.setArguments(args);
		transaction.add(R.id.sensor_list, sensorFragment, fragmentTag);
		transaction.commit();
		return sensorFragment;
	}
}
