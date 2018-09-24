package com.tbaraukova.ui.elasticsearch;

import com.intellij.openapi.ui.Messages;
import com.intellij.util.net.HTTPMethod;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

public class ElasticsearchRequestSender {
    private final String text;
    private final String url;
    private final String path;
    private final HTTPMethod method;

    public ElasticsearchRequestSender(String text, String url, String path, HTTPMethod method) {
        this.text = text;
        this.url = url;
        this.path = path;
        this.method = method;
    }

    public String getContent()
        throws IOException {
        String uri = url + path;
        Messages.showMessageDialog("Evaluate " + (StringUtils.isNotBlank(text) ? text + " " : "")
            + "on " + uri, "Information", Messages.getInformationIcon());
        Request request = getRequest(method, uri);
        if(StringUtils.isNotBlank(text)) {
            request.bodyString(text, ContentType.APPLICATION_JSON);
        }
        HttpResponse httpResponse = request.execute().returnResponse();
        Messages.showMessageDialog(httpResponse.getStatusLine().toString(), "Information",
            Messages.getInformationIcon());
        return IOUtils.toString(httpResponse.getEntity().getContent());
    }


    private Request getRequest(HTTPMethod method, String uri) {
        switch(method) {
            case PUT: {
                return Request.Put(uri);
            }
            case GET: {
                return Request.Get(uri);
            }
            case DELETE: {
                return Request.Delete(uri);
            }
            case POST:
            default: {
                return Request.Post(uri);
            }
        }
    }
}
