package com.tbaraukova.ui.elasticsearch;

import org.apache.commons.lang.StringUtils;

import java.util.concurrent.atomic.AtomicReference;

public enum ElasticsearchConnector {
    INSTANCE;

    private AtomicReference<Connection> connectionUrl = new AtomicReference<>(new Connection());

    public String getConnectionUrl() {
        return connectionUrl.get().getUrl();
    }

    public boolean isInitialized() {
        return connectionUrl.get().isInitialized();
    }

    public String host() {
        return connectionUrl.get().getHost();
    }

    public String port() {
        return connectionUrl.get().getPort() + "";
    }

    public String protocol() {
        return connectionUrl.get().getProtocol();
    }

    public ElasticsearchConnector host(String url) {
        connectionUrl.getAndUpdate(connection -> {
            connection.setHost(url);
            return connection;
        });
        return this;
    }

    public ElasticsearchConnector port(String url) {
        connectionUrl.getAndUpdate(connection -> {
            connection.setPort(StringUtils.isNumeric(url) ? Integer.parseInt(url) : -1);
            return connection;
        });
        return this;
    }

    public ElasticsearchConnector protocol(String url) {
        connectionUrl.getAndUpdate(connection -> {
            if(StringUtils.isNotEmpty(url)) {
                connection.setProtocol(url);
            }
            return connection;
        });
        return this;
    }

    public ElasticsearchConnector initialized(boolean initialized) {
        connectionUrl.getAndUpdate(connection -> {
            connection.setInitialized(initialized);
            return connection;
        });
        return this;
    }

}
