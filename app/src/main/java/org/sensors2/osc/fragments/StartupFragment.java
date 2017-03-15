package org.sensors2.osc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import org.sensors2.common.sensors.Parameters;
import org.sensors2.osc.R;
import org.sensors2.osc.activities.StartUpActivity;
import org.sensors2.osc.dispatch.Bundling;

public class StartupFragment extends Fragment {

    private CompoundButton activeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_start_up, container, false);

        activeButton = (CompoundButton) v.findViewById(R.id.active);
        StartUpActivity activity = (StartUpActivity) getActivity();
        activeButton.setOnCheckedChangeListener(activity);
        for (Parameters parameters : activity.getSensors()) {
            createSensorFragments((org.sensors2.osc.sensors.Parameters) parameters);
        }

        return v;
    }

    public void createSensorFragments(org.sensors2.osc.sensors.Parameters parameters) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        SensorFragment groupFragment = (SensorFragment) manager.findFragmentByTag(parameters.getName());

        if (groupFragment == null) {
            groupFragment = createFragment(parameters, manager);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.sensor_group, groupFragment, parameters.getName());
            transaction.commit();
        }
        addSensorToDispatcher(groupFragment);
    }

    private void addSensorToDispatcher(SensorFragment groupFragment) {
        StartUpActivity activity = (StartUpActivity) this.getActivity();
        activity.addSensorFragment(groupFragment);
    }

    public SensorFragment createFragment(org.sensors2.osc.sensors.Parameters parameters, FragmentManager manager) {
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
