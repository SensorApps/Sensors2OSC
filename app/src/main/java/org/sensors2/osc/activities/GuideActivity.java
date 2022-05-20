package org.sensors2.osc.activities;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.sensors2.osc.R;
import org.sensors2.osc.dispatch.Bundling;
import org.sensors2.osc.fragments.HelpSensorFragment;
import org.sensors2.osc.sensors.Parameters;

import java.util.List;

import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by thomas on 12.02.15.
 */
public class GuideActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        TextView availableSensorsHeadline = (TextView) findViewById(R.id.availSensorsHeadline);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Parameters> sensors = Parameters.GetSensors(sensorManager, this.getApplicationContext());
        availableSensorsHeadline.setText(sensors.size() + " " + availableSensorsHeadline.getText());
        for (Parameters parameters : sensors) {
            this.CreateSensorFragments(parameters);
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void CreateSensorFragments(Parameters parameters) {
        FragmentManager manager = getSupportFragmentManager();
        HelpSensorFragment groupFragment = (HelpSensorFragment) manager.findFragmentByTag(parameters.getName());
        if (groupFragment == null) {
            this.CreateFragment(parameters, manager);
        }
    }

    public void CreateFragment(Parameters parameters, FragmentManager manager) {
        FragmentTransaction transaction = manager.beginTransaction();
        HelpSensorFragment groupFragment = new HelpSensorFragment();
        Bundle args = new Bundle();
        args.putInt(Bundling.DIMENSIONS, parameters.getDimensions());
        args.putInt(Bundling.SENSOR_TYPE, parameters.getSensorType());
        args.putString(Bundling.OSC_PREFIX, parameters.getOscPrefix());
        args.putString(Bundling.NAME, parameters.getName());
        args.putString(Bundling.SENSOR_NAME, parameters.getSensorName());
        args.putFloat(Bundling.SENSOR_RANGE, parameters.getRange());
        args.putFloat(Bundling.RESOLUTION, parameters.getResolution());
        groupFragment.setArguments(args);
        transaction.add(R.id.sensor_group, groupFragment, parameters.getName());
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
