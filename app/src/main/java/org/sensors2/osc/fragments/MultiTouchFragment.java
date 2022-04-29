package org.sensors2.osc.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sensors2.common.dispatch.Measurement;
import org.sensors2.osc.R;
import org.sensors2.osc.activities.StartUpActivity;
import org.sensors2.osc.dispatch.OscDispatcher;
import org.sensors2.osc.dispatch.SensorConfiguration;

public class MultiTouchFragment extends Fragment {

    // this is an arbitrary limit - if a device supports more than a 8 finger multi touch this could simply be increased
    // alternatively, this could be added as a variable to the settings activity
    public static final int MAX_POINTER_COUNT = 8;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_multi_touch, container, false);

        StartUpActivity activity = (StartUpActivity) getActivity();
        v.findViewById(R.id.multi_touch_view).setOnTouchListener(activity);

        OscDispatcher dispatcher = (OscDispatcher) activity.getDispatcher();

        for(int i = 0; i < MAX_POINTER_COUNT; i++) {
            SensorConfiguration sensorConfiguration = new SensorConfiguration();
            sensorConfiguration.setSend(true);
            sensorConfiguration.setSensorType(Measurement.pointerIdToSensorType(i));
            sensorConfiguration.setOscParam("touch" + (i + 1));
            dispatcher.addSensorConfiguration(sensorConfiguration);
        }

        return v;
    }
}
