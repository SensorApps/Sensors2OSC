package org.sensors2.osc.dispatch;

import android.os.Looper;

/**
 * Created by thomas on 31.03.15.
 */
public class OscCommunication extends Thread {
    private OscHandler handler;

    public OscCommunication(String name) {
        super(name);
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new OscHandler(Looper.getMainLooper());
        Looper.loop();
    }

    public OscHandler getOscHandler() {
        return handler;
    }
}
