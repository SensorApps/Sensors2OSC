package org.sensors2.osc.dispatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import org.sensors2.common.dispatch.DataDispatcher;
import org.sensors2.common.dispatch.Measurement;
import org.sensors2.common.nfc.NfcActivity;
import org.sensors2.common.sensors.Parameters;
import org.sensors2.common.sensors.SensorActivity;
import org.sensors2.common.sensors.SensorCommunication;
import org.sensors2.common.sensors.Settings;
import org.sensors2.osc.R;
import org.sensors2.osc.activities.StartUpActivity;
import org.sensors2.osc.bluetoothSensors.BluetoothConnectionManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by thomas on 12.03.18.
 */

public class SensorService extends Service implements SensorActivity, SensorEventListener, NfcActivity {
    private static final String NOTIFICATION_CHANNEL_ID = "Sensors2OSC";
    private static final String NOTIFICATION_CHANNEL = "org.sensors2.osc";
    private static final String WAKELOCK_TAG = "org.sensors2.osc:wakelock";
    public static final int GEOLOC_PERMISSION_REQUEST = 1337;
    public static final int BT_PERMISSION_REQUEST = 1312;
    public final int NOTIFICATION_ID = 1;
    private final OscBinder binder = new OscBinder();
    private final BackgroundLocationListener locationListener;
    private OscDispatcher dispatcher;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private SensorCommunication sensorCommunication;
    private NfcAdapter nfcAdapter;
    private boolean isSendingData = false;
    private org.sensors2.osc.sensors.Settings settings;
    private PowerManager.WakeLock wakeLock;
    private BluetoothConnectionManager bluetoothConnections;

    public SensorService() {
        super();
        this.locationListener = new BackgroundLocationListener();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            this.bluetoothConnections = new BluetoothConnectionManager(this);
        }
    }

    @SuppressLint("WakelockTimeout")
    public void startSendingData() {
        if (!this.isSendingData) {
            initNotificationChannel();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                this.bluetoothConnections.setDispatcher(dispatcher);
            }
            int sensorRate = this.settings.getSensorRate();
            for (SensorConfiguration sensorConfig : this.dispatcher.getSensorConfigurations()) {
                if (sensorConfig.getSend()) {
                    if (sensorConfig.getSensorType() == Parameters.GEOLOCATION) {
                        this.bindLocation();
                    } else if (sensorConfig.getSensorType() == org.sensors2.osc.sensors.Parameters.BT_SENSOR){
                        this.bindBluetooth();
                    } else {
                        Sensor sensor = this.sensorManager.getDefaultSensor(sensorConfig.getSensorType());
                        this.sensorManager.registerListener(this, sensor, sensorRate);
                    }
                }
            }
            stopForeground(true);
            if (this.settings.getKeepScreenAlive()){
                if (this.wakeLock == null){
                    PowerManager powerManager = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
                    this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, WAKELOCK_TAG);
                }
                if (!this.wakeLock.isHeld()){
                    this.wakeLock.acquire();
                }
            }
            startForeground(NOTIFICATION_ID, makeNotification());
            this.isSendingData = true;
        }
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_LOW);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public boolean getIsSending() {
        return this.isSendingData;
    }

    public void setSensorActivation(int sensorType, boolean activation, Activity activity) {
        for (SensorConfiguration sensorConfig : this.dispatcher.getSensorConfigurations()) {
            if (sensorConfig.getSensorType() == sensorType) {
                sensorConfig.setSend(activation);
                break;
            }
        }
        if (sensorType == org.sensors2.common.sensors.Parameters.GEOLOCATION) {
            if (activation){
                this.checkForLocationPermission(activity);
                if (this.isSendingData) {
                    this.bindLocation();
                }
            }
            else {
                this.locationManager.removeUpdates(this.locationListener);
            }
        } else if (sensorType == org.sensors2.osc.sensors.Parameters.BT_SENSOR) {
            if (activation){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    this.checkForBluetoothPermission(activity);
                    if (this.isSendingData) {
                        this.bindBluetooth();
                    }
                }
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    this.bluetoothConnections.disconnect();
                }
            }

        } else if (activation) {
            Sensor sensor = this.sensorManager.getDefaultSensor(sensorType);
            this.sensorManager.registerListener(this, sensor, this.settings.getSensorRate());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void checkForBluetoothPermission(Activity activity) {
        this.bluetoothConnections.checkForPermissions(activity);
    }

    public boolean getSensorActivation(int sensorType) {
        for (SensorConfiguration sensorConfig : this.dispatcher.getSensorConfigurations()) {
            if (sensorConfig.getSensorType() == sensorType) {
                return sensorConfig.getSend();
            }
        }
        return false;
    }

    private void setUpSending() {
        if (this.dispatcher == null) {
            this.dispatcher = new OscDispatcher();
            this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            for (org.sensors2.osc.sensors.Parameters parameters : org.sensors2.osc.sensors.Parameters.GetSensors(sensorManager, getApplicationContext())) {
                SensorConfiguration sensorConfig = new SensorConfiguration();
                sensorConfig.setSensorType(parameters.getSensorType());
                sensorConfig.setOscParam(parameters.getOscPrefix());
                this.dispatcher.addSensorConfiguration(sensorConfig);
            }
            this.sensorCommunication = new SensorCommunication(this);
        }
        initNotificationChannel();
    }

    private Notification makeNotification() {
        Intent notificationIntent = new Intent(this, StartUpActivity.class);

        int intentFlag = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intentFlag = PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), notificationIntent, intentFlag);

        return new NotificationCompat.Builder(SensorService.this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.return_to_app))
                .setSmallIcon(R.drawable.sensors2osc_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sensors2osc))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setTicker(getText(R.string.app_name))
                .build();
    }

    public void stopSendingData() {
        if (this.wakeLock != null && this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        if (isSendingData) {
            stopForeground(true);
            this.locationManager.removeUpdates(this.locationListener);
            sensorManager.unregisterListener(this);
            isSendingData = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isSendingData) {
            this.sensorCommunication.dispatch(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public List<org.sensors2.common.sensors.Parameters> GetSensors(SensorManager sensorManager) {
        List<org.sensors2.common.sensors.Parameters> parameters = new ArrayList<>();

        // add Nfc sensor
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            parameters.add(new org.sensors2.osc.sensors.Parameters(nfcAdapter, this.getApplicationContext()));
        }
        // add device sensors
        parameters.addAll(org.sensors2.osc.sensors.Parameters.GetSensors(sensorManager, this.getApplicationContext()));
        return parameters;
    }

    @Override
    public DataDispatcher getDispatcher() {
        return this.dispatcher;
    }

    @Override
    public SensorManager getSensorManager() {
        return this.sensorManager;
    }

    @Override
    public Settings getSettings() {
        return this.settings;
    }

    public void setSettings(org.sensors2.osc.sensors.Settings settings) {
        boolean sensorListenerNeedsUpdate = this.isSendingData && this.settings.getSensorRate() != settings.getSensorRate();
        this.settings = settings;
        if (sensorListenerNeedsUpdate) {
            this.sensorManager.unregisterListener(this);
            for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
                sensorManager.registerListener(this, sensor, this.settings.getSensorRate());
            }
        }
    }

    @Override
    public NfcAdapter getNfcAdapter() {
        return this.nfcAdapter;
    }

    @Override
    public IBinder onBind(Intent intent) {
        setUpSending();
        return binder;
    }

    public void rebindBluetooth() {
        if (this.isSendingData){
            this.bindBluetooth();
        }
    }

    private void bindBluetooth() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            this.bluetoothConnections.connect();
        }
    }

    public class OscBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }

    public void rebindLocation() {
        if (this.isSendingData) {
            this.bindLocation();
        }
    }

    private void bindLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            location = this.locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER);
        } else {
            Location networkLocation = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location gpsLocation = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (networkLocation == null){
                location = gpsLocation;
            } else if (gpsLocation == null){
                location = networkLocation;
            } else {
                location = networkLocation.getTime() > gpsLocation.getTime() ? networkLocation : gpsLocation;
            }
        }
        if (location != null){
            this.dispatcher.dispatch(new Measurement(location));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            this.bindLocationUpdates(LocationManager.FUSED_PROVIDER);
        } else {
            this.bindLocationUpdates(LocationManager.GPS_PROVIDER);
            this.bindLocationUpdates(LocationManager.NETWORK_PROVIDER);
        }
    }

    private void checkForLocationPermission(Activity activity) {
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
                    (new String[0]), GEOLOC_PERMISSION_REQUEST);
        }
    }

    @SuppressLint("MissingPermission")
    private void bindLocationUpdates(String locationProvider) {
        if (this.locationManager.isProviderEnabled(locationProvider)) {
            this.locationManager.requestLocationUpdates(locationProvider, 5000, 1, this.locationListener);
        }
    }

    private class BackgroundLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(@NonNull Location location) {
            SensorService.this.dispatcher.dispatch(new Measurement(location));
        }
    }
}