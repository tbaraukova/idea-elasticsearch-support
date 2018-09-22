package com.tbaraukova.ui.elasticsearch;

import java.io.Serializable;

public class Connection implements Serializable {
    private String host = "localhost";
    private int port = 9200;
    private String protocol = Protocol.HTTP.toString();
    private boolean initialized = false;

    public Connection() {
    }

    public Connection(String host, int port, String protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setProtocol(String protocol) {
        if(protocol != null) {
            this.protocol = protocol;
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public String getUrl() {
        return protocol + "://" + host + (port > -1 ? ":" + port : "") + "/";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Connection)) {
            return false;
        }
        Connection that = (Connection) o;
        return port == that.port &&
            com.google.common.base.Objects.equal(host, that.host) &&
            com.google.common.base.Objects.equal(protocol, that.protocol);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(host, port, protocol);
    }

    @Override
    public String toString() {
        return "Connection{" +
            "host='" + host + '\'' +
            ", port=" + port +
            ", protocol='" + protocol + '\'' +
            '}';
    }
}
