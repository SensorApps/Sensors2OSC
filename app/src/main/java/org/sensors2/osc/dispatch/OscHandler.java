package org.sensors2.osc.dispatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.illposed.osc.OSCMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 31.03.15.
 */
public class OscHandler extends Handler {

	public OscHandler(Looper myLooper) {
		super(myLooper);
	}
	@Override
	public void handleMessage(Message message) {
		Bundle data = message.getData();
		float value = data.getFloat(Bundling.VALUE);
		String oscParameter = data.getString(Bundling.OSC_PARAMETER);
		OscConfiguration configuration = OscConfiguration.getInstance();

		if (configuration == null || configuration.getOscPort() == null) {
			return;
		}
		List<Object> changes = new ArrayList<Object>();
		changes.add(value);
		OSCMessage oscMessage = new OSCMessage("/" + oscParameter, changes);
		try {
			configuration.getOscPort().send(oscMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
