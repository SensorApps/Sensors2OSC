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

		switch (dimensions) {
			case 1:
				Create1DSensor(sensorType, oscPrefix);
				break;
			case 3:
				Create3DSensors(sensorType, oscPrefix);
				break;
			case 4:
				Create4DSensors(sensorType, oscPrefix);
				break;
			case 6:
				Create6DSensors(sensorType, oscPrefix);
				break;
		}

		return v;
	}

	private void Create6DSensors(int sensorType, String oscPrefix) {
		for (int i = 0; i < 6; i++) {
			String direction = "";
			switch (i) {
				case 0:
					direction = "X";
					break;
				case 1:
					direction = "Y";
					break;
				case 2:
					direction = "Z";
					break;
				case 3:
					direction = "dX";
					break;
				case 4:
					direction = "dY";
					break;
				case 5:
					direction = "dZ";
					break;
			}
			FragmentManager manager = getChildFragmentManager();
			String fragmentTag = oscPrefix + direction;
			SensorFragment sensorFragment = (SensorFragment) manager.findFragmentByTag(fragmentTag);
			if (sensorFragment == null) {
				sensorFragment = CreateSensorFragment(sensorType, oscPrefix, i, direction, manager, fragmentTag);
			}
			StartUpActivity activity = (StartUpActivity) this.getActivity();
			activity.addSensorFragment(sensorFragment);
		}
	}

	private void Create4DSensors(int sensorType, String oscPrefix) {
		for (int i = 0; i < 4; i++) {
			String direction = "";
			switch (i) {
				case 0:
					direction = "X";
					break;
				case 1:
					direction = "Y";
					break;
				case 2:
					direction = "Z";
					break;
				case 3:
					direction = "cos";
					break;
			}
			FragmentManager manager = getChildFragmentManager();
			String fragmentTag = oscPrefix + direction;
			SensorFragment sensorFragment = (SensorFragment) manager.findFragmentByTag(fragmentTag);
			if (sensorFragment == null) {
				sensorFragment = CreateSensorFragment(sensorType, oscPrefix, i, direction, manager, fragmentTag);
			}
			StartUpActivity activity = (StartUpActivity) this.getActivity();
			activity.addSensorFragment(sensorFragment);
		}
	}

	private void Create3DSensors(int sensorType, String oscPrefix) {
		for (int i = 0; i < 3; i++) {
			String direction = "";
			switch (i) {
				case 0:
					direction = "X";
					break;
				case 1:
					direction = "Y";
					break;
				case 2:
					direction = "Z";
					break;
			}
			FragmentManager manager = getChildFragmentManager();
			String fragmentTag = oscPrefix + direction;
			SensorFragment sensorFragment = (SensorFragment) manager.findFragmentByTag(fragmentTag);
			if (sensorFragment == null) {
				sensorFragment = CreateSensorFragment(sensorType, oscPrefix, i, direction, manager, fragmentTag);
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

	private void Create1DSensor(int sensorType, String oscPrefix) {
		FragmentManager manager = getChildFragmentManager();
		String fragmentTag = oscPrefix;
		SensorFragment sensorFragment = (SensorFragment) manager.findFragmentByTag(fragmentTag);
		if (sensorFragment == null) {
			sensorFragment = CreateSensorFragment(sensorType, oscPrefix, manager);
		}
		StartUpActivity activity = (StartUpActivity) this.getActivity();
		activity.addSensorFragment(sensorFragment);
	}

	private SensorFragment CreateSensorFragment(int sensorType, String oscPrefix, FragmentManager manager) {
		SensorFragment sensorFragment;
		FragmentTransaction transaction = manager.beginTransaction();
		sensorFragment = new SensorFragment();
		Bundle args = new Bundle();
		args.putInt("sensorType", sensorType);
		args.putString("oscPrefix", oscPrefix);
		sensorFragment.setArguments(args);
		transaction.add(R.id.sensor_list, sensorFragment, oscPrefix);
		transaction.commit();
		return sensorFragment;
	}

}
