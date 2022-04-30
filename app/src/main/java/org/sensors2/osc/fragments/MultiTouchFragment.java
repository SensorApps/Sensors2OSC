package org.sensors2.osc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sensors2.osc.R;
import org.sensors2.osc.activities.StartUpActivity;

import androidx.fragment.app.Fragment;

public class MultiTouchFragment extends Fragment {

    // this is an arbitrary limit - if a device supports more than a 8 finger multi touch this could simply be increased
    // alternatively, this could be added as a variable to the settings activity
    public static final int MAX_POINTER_COUNT = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_multi_touch, container, false);
        StartUpActivity activity = (StartUpActivity) getActivity();
        v.findViewById(R.id.multi_touch_view).setOnTouchListener(activity);
        return v;
    }
}
