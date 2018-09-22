package com.tbaraukova.ui.elasticsearch;

import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NonEmptyInputValidator;
import com.intellij.openapi.vfs.VirtualFile;
import com.tbaraukova.ui.elasticsearch.connections.Connection;
import com.tbaraukova.ui.elasticsearch.connections.ConnectionHolder;
import com.tbaraukova.ui.elasticsearch.connections.Connections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

public class ElasticsearchConnectorAction extends AnAction {

    public static final String ELASTICSEARCH_QUERY_JSON = "elasticsearch-query.json";

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        ConnectionHolder connectionHolder = ServiceManager.getService(ConnectionHolder.class);
        try {
            Connections state = connectionHolder.getState();
            if(state == null) {
                connectionHolder.noStateLoaded();
                state = connectionHolder.getState();
            }
            List<Connection> connections = state.getConnections();
            Connection latestConnection = connections.get(connections.size() - 1);
            String host = Messages.showInputDialog(project, "What is database host name?", "Input Host Name",
                Messages.getQuestionIcon(), latestConnection.getHost(), new NonEmptyInputValidator());
            if(host == null) {
                return;
            }
            String port = Messages.showInputDialog(project, "What is database port name?", "Input Port Name",
                Messages.getQuestionIcon(), latestConnection.getPort() + "", new NonEmptyInputValidator());
            if(port == null) {
                return;
            }
            int protocol = Messages.showChooseDialog(project,
                "What is database protocol name (leave empty for \"http\" by default)?", "Input Protocol",
                Messages.getQuestionIcon(),
                Protocol.names(), Protocol.HTTP.toString());
            if(protocol == -1) {
                return;
            }

            Connection currentConnection = new Connection(host, Integer.valueOf(port),
                Protocol.byOrdinal(protocol).toString());
            HttpResponse httpResponse = Request.Get(currentConnection.getUrl()).execute().returnResponse();
            if(httpResponse.getStatusLine().getStatusCode() != 200) {
                Messages.showMessageDialog(project, httpResponse.getStatusLine().getReasonPhrase(), "Connection Error!",
                    Messages.getErrorIcon());
                return;
            }
            state.getConnections().remove(currentConnection);
            state.getConnections().add(currentConnection);
            currentConnection.setInitialized(true);
            Messages.showMessageDialog(project, IOUtils.toString(httpResponse.getEntity().getContent()), "Information",
                Messages.getInformationIcon());
            VirtualFile file = ScratchFileService.getInstance().findFile(ScratchRootType.getInstance(),
                ELASTICSEARCH_QUERY_JSON, ScratchFileService.Option.create_if_missing);
            FileEditorManager.getInstance(project).openFile(file, true);
        } catch(Exception ex) {
            Messages.showMessageDialog(project, ex.getMessage(), "Error", Messages.getErrorIcon());
        }
    }
}
