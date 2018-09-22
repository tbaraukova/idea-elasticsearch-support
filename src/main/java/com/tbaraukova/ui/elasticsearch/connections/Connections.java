package com.tbaraukova.ui.elasticsearch.connections;

import com.google.common.collect.Lists;
import com.intellij.util.xmlb.annotations.Property;
import java.util.List;

public class Connections {

    @Property
    private List<Connection> connections;

    public Connections() {
        connections = Lists.newArrayList(new Connection());
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

}
