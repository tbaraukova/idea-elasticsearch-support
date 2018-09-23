package com.tbaraukova.ui.elasticsearch.queries;

import com.google.common.collect.Lists;
import com.intellij.util.xmlb.annotations.Property;
import java.util.List;

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
}
