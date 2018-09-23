package com.tbaraukova.ui.elasticsearch.queries;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ElasticsearchQueries", storages = @Storage("elasticsearch-queries.xml"), defaultStateAsResource = true)
public class QueryHolder implements PersistentStateComponent<Queries> {

    public Queries connections;

    @Nullable
    @Override
    public Queries getState() {
        return connections;
    }

    @Override
    public void loadState(@NotNull Queries state) {
        this.connections = state;
    }

    @Override
    public void noStateLoaded() {
        this.connections = new Queries();
    }
}