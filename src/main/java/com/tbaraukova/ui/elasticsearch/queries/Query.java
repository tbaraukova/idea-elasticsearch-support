package com.tbaraukova.ui.elasticsearch.queries;

import com.google.common.base.Objects;
import com.intellij.util.xmlb.annotations.Property;

public class Query {
    @Property
    private String path = "/_search";
    @Property
    private String method = "POST";

    public Query() {
    }

    public Query(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Query)) {
            return false;
        }
        Query query = (Query) o;
        return Objects.equal(path, query.path) &&
            Objects.equal(method, query.method);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path, method);
    }

    @Override
    public String toString() {
        return "Query{" +
            "path='" + path + '\'' +
            ", method='" + method + '\'' +
            '}';
    }
}
