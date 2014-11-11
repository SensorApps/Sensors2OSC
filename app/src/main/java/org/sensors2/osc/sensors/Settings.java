package org.sensors2.osc.sensors;

import android.content.SharedPreferences;

/**
 * Created by thomas on 05.11.14.
 */
public class Settings extends org.sensors2.common.sensors.Settings {

	private String host;
	private int port;

	public Settings(SharedPreferences preferences) {
		super(preferences);
		this.setHost(preferences);
		this.setPort(preferences);
	}

	public int getPort() {
		return port;
	}

	private void setPort(SharedPreferences preferences) {
		this.port = Integer.valueOf(preferences.getString("pref_comm_port", "9000"));
	}

	public String getHost() {
		return host;
	}

	private void setHost(SharedPreferences preferences) {
		this.host = preferences.getString("pref_comm_host", "localhost");
	}
}
