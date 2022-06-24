package org.sensors2.osc.dispatch;

import com.illposed.osc.OSCPortOut;

import java.net.InetAddress;

public class OscConfiguration {
    private static OscConfiguration instance;
    private OSCPortOut oscPort;
    private String host;
    private int port;
    private boolean sendAsBundle;
    private boolean keepScreenAlive;

    private OscConfiguration() {
        this.oscPort = null;
        this.host = null;
        this.port = 0;
    }

    public static OscConfiguration getInstance() {
        if (instance == null) {
            instance = new OscConfiguration();
        }
        return instance;
    }

    public void setHost(String host) {
        this.host = host;
        this.oscPort = null;
    }

    public void setPort(int port) {
        this.port = port;
        this.oscPort = null;
    }

    public OSCPortOut getOscPort() {
        if (this.oscPort == null) {
            try {
                InetAddress address = InetAddress.getByName(this.host);
                this.oscPort = new OSCPortOut(address, this.port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.oscPort;
    }

    public void setSendAsBundle(boolean sendAsBundle) {
        this.sendAsBundle = sendAsBundle;
    }

    public boolean getSendAsBundle() {
        return this.sendAsBundle;
    }

    public void setKeepScreenAlive(boolean keepScreenAlive) { this.keepScreenAlive = keepScreenAlive; }
    public boolean getKeepScreenAlive() {
        return this.keepScreenAlive;
    }

}
