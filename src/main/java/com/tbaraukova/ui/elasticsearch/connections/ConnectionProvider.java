package com.tbaraukova.ui.elasticsearch.connections;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NonEmptyInputValidator;
import com.tbaraukova.ui.elasticsearch.Protocol;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.jetbrains.annotations.Nullable;

public class ConnectionProvider {

    private Project project;
    private Connections connections;

    public ConnectionProvider(Project project, Connections connections) {
        this.project = project;
        this.connections = connections;
    }

    @Nullable
    public Connection getConnection() throws IOException {
        String host = readHost();
        if(host == null) {
            return null;
        }
        String port = readPort();
        if(port == null) {
            return null;
        }
        int protocol = readProtocol();
        if(protocol == -1) {
            return null;
        }

        Connection currentConnection = new Connection(host, Integer.valueOf(port),
            Protocol.byOrdinal(protocol).toString());
        if(testConnection(project, currentConnection) == null) {
            return null;
        }
        connections.moveToEnd(currentConnection);
        return currentConnection;
    }

    private int readProtocol() {
        return Messages.showChooseDialog(project,
            "What is database protocol name (leave empty for \"http\" by default)?", "Input Protocol",
            Messages.getQuestionIcon(), Protocol.names(), connections.getLatest().getProtocol());
    }

    @Nullable
    private String readPort() {
        return Messages.showEditableChooseDialog("What is database port name?", "Input Port Name",
            Messages.getQuestionIcon(),
            connections.stream().map(i -> i.getPort() + "").distinct().toArray(String[]::new),
            connections.getLatest().getPort() + "", new NonEmptyInputValidator());
    }

    @Nullable
    private String readHost() {
        return Messages.showEditableChooseDialog("What is database host name?", "Input Host Name",
            Messages.getQuestionIcon(), connections.stream().map(Connection::getHost).distinct().toArray(String[]::new),
            connections.getLatest().getHost(), new NonEmptyInputValidator());
    }

    @Nullable
    private HttpResponse testConnection(Project project, Connection currentConnection) throws IOException {
        HttpResponse httpResponse = Request.Get(currentConnection.getUrl()).execute().returnResponse();
        if(httpResponse.getStatusLine().getStatusCode() != HttpResponseStatus.OK.code()) {
            Messages.showMessageDialog(project, httpResponse.getStatusLine().getReasonPhrase(), "Connection Error!",
                Messages.getErrorIcon());
            return null;
        }
        Messages.showMessageDialog(project, IOUtils.toString(httpResponse.getEntity().getContent()), "Information",
            Messages.getInformationIcon());
        return httpResponse;
    }


}
