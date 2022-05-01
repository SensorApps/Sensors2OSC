package org.sensors2.osc.dispatch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.NfcAdapter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import org.sensors2.common.dispatch.DataDispatcher;
import org.sensors2.common.nfc.NfcActivity;
import org.sensors2.common.sensors.Parameters;
import org.sensors2.common.sensors.SensorActivity;
import org.sensors2.common.sensors.SensorCommunication;
import org.sensors2.common.sensors.Settings;
import org.sensors2.osc.R;
import org.sensors2.osc.activities.StartUpActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;

/**
 * Created by thomas on 12.03.18.
 */

public class SensorService extends Service implements SensorActivity, SensorEventListener, NfcActivity {
    private static final String NOTIFICATION_CHANNEL_ID = "Sensors2OSC";
    private static final String NOTIFICATION_CHANNEL = "org.sensors2.osc";
    public final int NOTIFICATION_ID = 1;
    private final OscBinder binder = new OscBinder();
    private OscDispatcher dispatcher;
    private SensorManager sensorManager;
    private SensorCommunication sensorCommunication;
    private NfcAdapter nfcAdapter;
    private boolean isSendingData = false;
    private org.sensors2.osc.sensors.Settings settings;

    public SensorService() {
        super();
    }

    public void startSendingData() {
        if (!this.isSendingData) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT));
            }
            int sensorRate = this.settings.getSensorRate();
            for (SensorConfiguration sensorConfig : this.dispatcher.getSensorConfigurations()) {
                if (sensorConfig.getSend()) {
                    Sensor sensor = this.sensorManager.getDefaultSensor(sensorConfig.getSensorType());
                    this.sensorManager.registerListener(this, sensor, sensorRate);
                }
            }
            stopForeground(true);
            startForeground(NOTIFICATION_ID, makeNotification());
            this.isSendingData = true;
        }
    }

    public boolean getIsSending() {
        return this.isSendingData;
    }

    public void setSensorActivation(int sensorType, boolean activation) {
        for (SensorConfiguration sensorConfig : this.dispatcher.getSensorConfigurations()) {
            if (sensorConfig.getSensorType() == sensorType) {
                sensorConfig.setSend(activation);
                break;
            }
        }
        if (activation) {
            Sensor sensor = this.sensorManager.getDefaultSensor(sensorType);
            this.sensorManager.registerListener(this, sensor, this.settings.getSensorRate());
        }
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
            for (org.sensors2.osc.sensors.Parameters parameters : org.sensors2.osc.sensors.Parameters.GetSensors(sensorManager, getApplicationContext())) {
                SensorConfiguration sensorConfig = new SensorConfiguration();
                sensorConfig.setSensorType(parameters.getSensorType());
                sensorConfig.setOscParam(parameters.getOscPrefix());
                this.dispatcher.addSensorConfiguration(sensorConfig);
            }
            this.sensorCommunication = new SensorCommunication(this);
        }
    }

    private Notification makeNotification() {
        Intent notificationIntent = new Intent(this, StartUpActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(SensorService.this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.return_to_app))
                .setSmallIcon(R.drawable.sensors2osc_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.sensors2osc))
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.app_name))
                .build();
    }


    public void stopSendingData() {
        if (isSendingData) {
            stopForeground(true);
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
    public List<Parameters> GetSensors(SensorManager sensorManager) {
        List<Parameters> parameters = new ArrayList<>();

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

    public class OscBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }
}
