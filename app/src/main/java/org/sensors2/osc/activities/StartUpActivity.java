package org.sensors2.osc.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.CompoundButton;

import org.sensors2.common.dispatch.Measurement;
import org.sensors2.osc.R;
import org.sensors2.osc.dispatch.OscConfiguration;
import org.sensors2.osc.dispatch.OscDispatcher;
import org.sensors2.osc.dispatch.SensorConfiguration;
import org.sensors2.osc.dispatch.SensorService;
import org.sensors2.osc.fragments.MultiTouchFragment;
import org.sensors2.osc.fragments.SensorFragment;
import org.sensors2.osc.fragments.StartupFragment;
import org.sensors2.osc.sensors.Settings;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static org.sensors2.osc.fragments.MultiTouchFragment.MAX_POINTER_COUNT;

public class StartUpActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnTouchListener {

    private final List<SensorFragment> sensorFragments = new ArrayList<>();
    private Settings settings;
    private boolean active;
    private SensorService sensorService;
    private final ServiceConnection sensorServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SensorService.OscBinder binder = (SensorService.OscBinder) service;
            StartUpActivity.this.sensorService = binder.getService();
            StartUpActivity.this.sensorService.setSettings(settings);
            StartUpActivity.this.active = StartUpActivity.this.sensorService.getIsSending();
            ((CompoundButton) findViewById(R.id.active)).setChecked(StartUpActivity.this.active);
            OscDispatcher dispatcher = (OscDispatcher) StartUpActivity.this.sensorService.getDispatcher();

            // Setup multitouch
            for (int i = 0; i < MAX_POINTER_COUNT; i++) {
                SensorConfiguration sensorConfiguration = new SensorConfiguration();
                sensorConfiguration.setSend(true);
                sensorConfiguration.setSensorType(Measurement.pointerIdToSensorType(i));
                sensorConfiguration.setOscParam("touch" + (i + 1));
                dispatcher.addSensorConfiguration(sensorConfiguration);
            }

            // Setup location
            SensorConfiguration location = new SensorConfiguration();
            location.setSendDuplicates(true);
            location.setSend(true);
            location.setSensorType(0);
            location.setOscParam("location");
            dispatcher.addSensorConfiguration(location);

            // Hookup sensor activity buttons with service
            for (SensorFragment sensorFragment : StartUpActivity.this.sensorFragments) {
                sensorFragment.setSensorService(StartUpActivity.this.sensorService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.settings = this.loadSettings();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        StartupFragment startupFragment = (StartupFragment) fm.findFragmentByTag("sensorlist");
        if (startupFragment == null) {
            startupFragment = new StartupFragment();
            transaction.add(R.id.container, startupFragment, "sensorlist");
            transaction.commit();
        }
        startService(new Intent(this, SensorService.class));
        bindService(new Intent(this, SensorService.class), this.sensorServiceConnection, BIND_AUTO_CREATE);

        Toolbar toolbar = findViewById(R.id.action_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationIcon(R.drawable.sensors2osc);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private Settings loadSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Settings settings = new Settings(preferences);
        OscConfiguration oscConfiguration = OscConfiguration.getInstance();
        oscConfiguration.setHost(settings.getHost());
        oscConfiguration.setPort(settings.getPort());
        oscConfiguration.setSendAsBundle(settings.getSetAsBundle());
        return settings;
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_up, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_guide: {
                Intent intent = new Intent(this, GuideActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    @SuppressLint("NewApi")
    protected void onResume() {
        super.onResume();
        this.settings = this.loadSettings();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            this.setRequestedOrientation(this.getCurrentOrientation());
            if (sensorService != null){
                sensorService.startSendingData();
            }
        } else {
            sensorService.stopSendingData();
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        active = isChecked;
    }

    public void onStartMultiTouch(View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.container, new MultiTouchFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public int getCurrentOrientation() {
        final Display display = this.getWindowManager().getDefaultDisplay();
        final int width, height;
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        switch (display.getRotation()) {
            case Surface.ROTATION_90:
                if (width > height) {
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                }
            case Surface.ROTATION_180:
                if (height > width) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                }
            case Surface.ROTATION_270:
                if (width > height) {
                    return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else {
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }
            case Surface.ROTATION_0:
            default:
                if (height > width) {
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else {
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(sensorServiceConnection);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.sensorService.getIsSending()) {
            int width = v.getWidth();
            int height = v.getHeight();
            for (Measurement measurement : Measurement.measurements(event, width, height)) {
                this.sensorService.getDispatcher().dispatch(measurement);
            }
        }
        return false;
    }

    public void registerFragment(SensorFragment sensorFragment) {
        this.sensorFragments.add(sensorFragment);
    }
}
