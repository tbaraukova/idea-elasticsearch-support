package com.tbaraukova.ui.elasticsearch.connections;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ElasticsearchConnectionSettings", storages = @Storage("elasticsearch-connections.xml"), defaultStateAsResource = true)
public class ConnectionHolder implements PersistentStateComponent<Connections> {

    public Connections connections;

    @Nullable
    @Override
    public Connections getState() {
        return connections;
    }

    @Override
    public void loadState(@NotNull Connections state) {
        state.getConnections().forEach(item -> item.setInitialized(false));
        this.connections = state;
    }

    @Override
    public void noStateLoaded() {
        this.connections = new Connections();
    }
}
