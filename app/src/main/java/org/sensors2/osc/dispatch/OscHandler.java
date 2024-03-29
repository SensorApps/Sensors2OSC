package org.sensors2.osc.dispatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 31.03.15.
 */
public class OscHandler extends Handler {

    public OscHandler(Looper looper) {
       super(looper);
    }

    @Override
    public void handleMessage(Message message) {
        Bundle data = message.getData();
        float[] values = data.getFloatArray(Bundling.VALUES);
        String stringValue = data.getString(Bundling.STRING_VALUE);
        String oscParameter = data.getString(Bundling.OSC_PARAMETER);
        OscConfiguration configuration = OscConfiguration.getInstance();

        if (configuration == null || configuration.getOscPort() == null) {
            return;
        }
        boolean sendAsBundle = configuration.getSendAsBundle();
        List<Object> changes = new ArrayList<>();
        if (values != null) {
            for (float value : values) {
                changes.add(value);
            }
        }
        if (stringValue != null) {
            changes.add(stringValue);
        }
        OSCMessage oscMessage = new OSCMessage("/" + oscParameter, changes);
        OSCPacket packet;
        if (sendAsBundle) {
            OSCBundle oscBundle = new OSCBundle();
            oscBundle.addPacket(oscMessage);
            packet = oscBundle;
        } else {
            packet = oscMessage;
        }
        try {
            configuration.getOscPort().send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
