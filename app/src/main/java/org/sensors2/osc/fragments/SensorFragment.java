package org.sensors2.osc.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sensors2.common.sensors.Parameters;
import org.sensors2.osc.R;
import org.sensors2.osc.activities.StartUpActivity;
import org.sensors2.osc.dispatch.Bundling;
import org.sensors2.osc.dispatch.SensorConfiguration;
import org.sensors2.osc.dispatch.SensorService;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * Created by thomas on 09.11.14.
 */
public class SensorFragment extends Fragment {

    private final SensorConfiguration sensorConfiguration;
    private CompoundButton activeButton;
    private SensorService sensorService;

    public SensorFragment() {
        super();
        this.sensorConfiguration = new SensorConfiguration();
    }

    public void setSensorService(SensorService sensorService) {
        this.sensorService = sensorService;
        this.activeButton.setChecked(this.sensorService.getSensorActivation(this.sensorConfiguration.getSensorType()));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        assert args != null;
        this.sensorConfiguration.setSensorType(args.getInt(Bundling.SENSOR_TYPE));
        String name = args.getString(Bundling.NAME);

        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.sensor, null);

        TextView groupName = v.findViewById(R.id.group_name);
        groupName.setText(name);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) groupName.getLayoutParams();
        layoutParams.addRule(RelativeLayout.LEFT_OF,R.id.active);
        groupName.setLayoutParams((layoutParams));

        TextView oscParam = v.findViewById(R.id.osc_prefix);
        oscParam.setText("/" + args.getString(Bundling.OSC_PREFIX));
        layoutParams = (RelativeLayout.LayoutParams) oscParam.getLayoutParams();
        layoutParams.addRule(RelativeLayout.LEFT_OF,R.id.active);
        layoutParams.addRule(RelativeLayout.BELOW,R.id.group_name);
        oscParam.setLayoutParams((layoutParams));

        StartUpActivity activity = (StartUpActivity) getActivity();
        assert activity != null;
        activity.registerFragment(this);

        this.activeButton = v.findViewById(R.id.active);
        this.activeButton.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (SensorFragment.this.sensorService != null) {
                //TODO: Wait for setting permissions before calling sensorService
                if (checked && this.sensorConfiguration.getSensorType() == Parameters.GEOLOCATION) {
                    askForLocationPermission(activity);
                }
                SensorFragment.this.sensorService.setSensorActivation(SensorFragment.this.sensorConfiguration.getSensorType(), checked);
            }
        });

        return v;
    }

    private void askForLocationPermission(Activity activity) {
        int loc = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray
                    (new String[0]), 1);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
