package org.sensors2.osc.dispatch;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

/**
 * Created by thomas on 09.05.17.
 */

public class OscService extends Service {

    public final int NOTIFICATION_ID= 1;
    private boolean hasForeground = false;

    public void start(Intent intent, int icon, String title, String description) {
        stopForeground();
        startForeground(NOTIFICATION_ID, makeNotification(intent, icon, title, description));
        hasForeground = true;
    }

    private Notification makeNotification(Intent intent, int icon, String title, String description) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        return new NotificationCompat.Builder(OscService.this)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(description)
                .setOngoing(true)
                .setContentIntent(pi)
                .setWhen(System.currentTimeMillis())
                .build();
    }


    public void stop() {
        stopForeground();
    }


    private void stopForeground() {
        if (hasForeground) {
            stopForeground(true);
            hasForeground = false;
        }
    }

    public class OscBinder extends Binder {
        public OscService getService() {
            return OscService.this;
        }
    }
    private final OscBinder binder = new OscBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
