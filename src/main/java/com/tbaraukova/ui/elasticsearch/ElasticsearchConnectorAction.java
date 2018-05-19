package com.tbaraukova.ui.elasticsearch;

import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NonEmptyInputValidator;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

public class ElasticsearchConnectorAction extends AnAction {

    private static final ElasticsearchConnector ELASTICSEARCH_CONNECTOR = ElasticsearchConnector.INSTANCE;
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
        try {
            String host = Messages.showInputDialog(project, "What is database host name?", "Input Host Name",
                Messages.getQuestionIcon(),
                ELASTICSEARCH_CONNECTOR.isInitialized() ? ELASTICSEARCH_CONNECTOR.host() : "localhost",
                new NonEmptyInputValidator());
            if(host == null) {
                return;
            }
            String port = Messages.showInputDialog(project, "What is database port name?", "Input Port Name",
                Messages.getQuestionIcon(),
                ELASTICSEARCH_CONNECTOR.isInitialized() ? ELASTICSEARCH_CONNECTOR.port() : "9200",
                new NonEmptyInputValidator());
            if(port == null) {
                return;
            }
            String protocol = Messages.showInputDialog(project,
                "What is database protocol name (leave empty for \"http\" by default)?", "Input Protocol",
                Messages.getQuestionIcon(),
                ELASTICSEARCH_CONNECTOR.isInitialized() ? ELASTICSEARCH_CONNECTOR.protocol() : "",
                PROTOCOL_VALIDATOR);
            if(protocol == null) {
                return;
            }

            ELASTICSEARCH_CONNECTOR.host(host).port(port).protocol(protocol).initialized(true);
            Content content = Request.Get(ELASTICSEARCH_CONNECTOR.getConnectionUrl()).execute().returnContent();
            Messages.showMessageDialog(project, content.asString(), "Information", Messages.getInformationIcon());
            VirtualFile file = ScratchFileService.getInstance().findFile(ScratchRootType.getInstance(),
                ELASTICSEARCH_QUERY_JSON, ScratchFileService.Option.create_if_missing);
            FileEditorManager.getInstance(project).openFile(file, true);
        } catch(Exception ex) {
            Messages.showMessageDialog(project, ex.getMessage(), "Error", Messages.getErrorIcon());
        }
    }
}
