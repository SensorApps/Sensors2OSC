package org.sensors2.osc.sensors;

import android.content.SharedPreferences;

/**
 * Created by thomas on 05.11.14.
 */
public class Settings extends org.sensors2.common.sensors.Settings {

    private final String host;
    private final int port;
    private final boolean enableNfc;

    public Settings(SharedPreferences preferences) {
        super(preferences);
        this.host = this.setHost(preferences);
        this.port = this.setPort(preferences);
        this.enableNfc = this.setEnableNfc(preferences);
    }

    private boolean setEnableNfc(SharedPreferences preferences) {
        return preferences.getBoolean("pref_enable_nfc", false);
    }

    public boolean getEnableNfc(){
        return this.enableNfc;
    }

    public int getPort() {
        return port;
    }

    private int setPort(SharedPreferences preferences) {
        return Integer.parseInt(preferences.getString("pref_comm_port", "9000"));
    }

    public String getHost() {
        return host;
    }

    private String setHost(SharedPreferences preferences) {
        return preferences.getString("pref_comm_host", "localhost");
    }
}
