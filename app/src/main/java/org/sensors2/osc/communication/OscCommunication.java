package org.sensors2.osc.communication;

/**
 * Created by thomas on 10.11.14.
 */import android.os.AsyncTask;

import com.illposed.osc.OSCMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 19.09.14.
 */
public class OscCommunication extends AsyncTask<String, Void, Boolean> {

	private final OscConfiguration operation;

	public OscCommunication(OscConfiguration operation) {
		this.operation = operation;
	}

	@Override
	protected Boolean doInBackground(String... strings) {
		if (this.operation.getOscPort() == null) {
			return false;
		}
		List<Object> changes = new ArrayList<Object>();
		changes.add(strings[1]);
		try {
			OSCMessage message = new OSCMessage("/" + strings[0], changes);
			this.operation.getOscPort().send(message);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}