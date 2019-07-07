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
import com.intellij.openapi.vfs.VirtualFile;
import com.tbaraukova.ui.elasticsearch.connections.Connection;
import com.tbaraukova.ui.elasticsearch.connections.ConnectionHolder;
import com.tbaraukova.ui.elasticsearch.connections.ConnectionProvider;
import com.tbaraukova.ui.elasticsearch.connections.Connections;
import java.io.IOException;

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
            Connection currentConnection = new ConnectionProvider(project, state).getConnection();
            if(currentConnection == null) {
                return;
            }
            currentConnection.setInitialized(true);
            openQueryFile(project);
        } catch(Exception ex) {
            Messages.showMessageDialog(project, ex.getMessage(), "Error", Messages.getErrorIcon());
        }
    }

    private void openQueryFile(Project project) throws IOException {
        VirtualFile file = ScratchFileService.getInstance().findFile(ScratchRootType.getInstance(),
            ELASTICSEARCH_QUERY_JSON, ScratchFileService.Option.create_if_missing);
        FileEditorManager.getInstance(project).openFile(file, true);
    }

}
