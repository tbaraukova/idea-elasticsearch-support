package com.tbaraukova.ui.elasticsearch;

import java.net.URI;

public class Connection {
    private String host = "localhost";
    private int port = 9200;
    private String protocol = "http";

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setProtocol(String protocol) {
        if (protocol != null) {
            this.protocol = protocol;
        }
    }

    public String getUrl() {
        return protocol + "://" + host + (port > -1 ? ":" + port : "") + "/";
    }
}
