package org.sensors2.osc.fragments;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import org.sensors2.osc.R;
import org.sensors2.osc.activities.StartUpActivity;
import org.sensors2.osc.dispatch.Bundling;
import org.sensors2.osc.sensors.Parameters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class StartupFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_start_up, container, false);

        CompoundButton activeButton = v.findViewById(R.id.active);
        StartUpActivity activity = (StartUpActivity) getActivity();
        activeButton.setOnCheckedChangeListener(activity);
        assert activity != null;
        SensorManager sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        for (Parameters parameters : org.sensors2.osc.sensors.Parameters.GetSensors(sensorManager, activity.getApplicationContext())) {
            createSensorFragments(parameters);
        }

        return v;
    }

    public void createSensorFragments(org.sensors2.osc.sensors.Parameters parameters) {
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        SensorFragment groupFragment = (SensorFragment) manager.findFragmentByTag(parameters.getName());

        if (groupFragment == null) {
            groupFragment = createFragment(parameters);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.sensor_group, groupFragment, parameters.getName());
            transaction.commit();
        }
    }

    public SensorFragment createFragment(org.sensors2.osc.sensors.Parameters parameters) {
        SensorFragment groupFragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putInt(Bundling.DIMENSIONS, parameters.getDimensions());
        args.putInt(Bundling.SENSOR_TYPE, parameters.getSensorType());
        args.putString(Bundling.OSC_PREFIX, parameters.getOscPrefix());
        args.putString(Bundling.NAME, parameters.getName());
        groupFragment.setArguments(args);
        return groupFragment;
    }
}
