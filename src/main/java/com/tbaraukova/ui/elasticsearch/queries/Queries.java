package com.tbaraukova.ui.elasticsearch.queries;

import com.google.common.collect.Lists;
import com.intellij.util.xmlb.annotations.Property;
import java.util.List;
import java.util.stream.Stream;

public class Queries {
    @Property
    private List<Query> queries;

    public Queries() {
        queries = Lists.newArrayList(new Query());
    }

    public List<Query> getQueries() {
        return queries;
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }

    public Stream<Query> stream() {
        return queries.stream();
    }

    public void moveToEnd(Query query) {
        queries.remove(query);
        queries.add(query);
    }

    public Query getLatest() {
        return queries.get(queries.size() - 1);
    }
}
