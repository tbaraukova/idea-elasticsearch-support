package com.tbaraukova.ui.elasticsearch;

import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NonEmptyInputValidator;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;

public class ElasticsearchConnectorAction extends AnAction {

    public static final String ELASTICSEARCH_QUERY_JSON = "elasticsearch-query.json";
    public static final InputValidator PROTOCOL_VALIDATOR = new InputValidator() {
        @Override
        public boolean checkInput(String inputString) {
            return StringUtils.isEmpty(inputString) || "http".equals(inputString) || "https".equals(
                inputString);
        }

        @Override
        public boolean canClose(String inputString) {
            return checkInput(inputString);
        }
    };

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        ConnectionHolder connectionHolder = ServiceManager.getService(project, ConnectionHolder.class);
        try {
            List<Connection> state = connectionHolder.getState();
            if(state == null) {
                connectionHolder.noStateLoaded();
                state = connectionHolder.getState();
            }
            Connection latestConnection = state.get(state.size() - 1);
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
            String protocol = Messages.showInputDialog(project,
                "What is database protocol name (leave empty for \"http\" by default)?", "Input Protocol",
                Messages.getQuestionIcon(), latestConnection.getProtocol(), PROTOCOL_VALIDATOR);
            if(protocol == null) {
                return;
            }

            Connection currentConnection = new Connection(host, Integer.valueOf(port), protocol);
            HttpResponse httpResponse = Request.Get(currentConnection.getUrl()).execute().returnResponse();
            if(httpResponse.getStatusLine().getStatusCode() != 200) {
                Messages.showMessageDialog(project, httpResponse.getStatusLine().getReasonPhrase(), "Connection Error!",
                    Messages.getErrorIcon());
                return;
            }
            state.remove(currentConnection);
            state.add(currentConnection);
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
