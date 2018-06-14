package com.tbaraukova.ui.elasticsearch;

import com.google.common.collect.Lists;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ElasticsearchConnectionSettings", storages = @Storage("elasticsearch-connections.xml"))
public class ConnectionHolder implements PersistentStateComponent<List<Connection>> {

    private List<Connection> connections;

    @Nullable
    @Override
    public List<Connection> getState() {
        return connections;
    }

    @Override
    public void loadState(@NotNull List<Connection> state) {
        state.forEach(item -> item.setInitialized(false));
        this.connections = state;
    }

    @Override
    public void noStateLoaded() {
        this.connections = Lists.newArrayList(new Connection());
    }
}
