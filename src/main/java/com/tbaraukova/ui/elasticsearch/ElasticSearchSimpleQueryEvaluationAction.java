package com.tbaraukova.ui.elasticsearch;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NonEmptyInputValidator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.net.HTTPMethod;
import com.tbaraukova.ui.elasticsearch.connections.Connection;
import com.tbaraukova.ui.elasticsearch.connections.ConnectionHolder;
import com.tbaraukova.ui.elasticsearch.connections.Connections;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.jetbrains.annotations.Nullable;

public class ElasticSearchSimpleQueryEvaluationAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setVisible(isVisible(e.getProject(), e.getData(CommonDataKeys.EDITOR),
            e.getData(PlatformDataKeys.VIRTUAL_FILE)));
    }

    protected boolean isVisible(Project project, Editor editor, VirtualFile virtualFile) {
        Connections connections = ServiceManager.getService(ConnectionHolder.class).getState();
        if(connections == null) {
            return false;
        }
        List<Connection> state = connections.getConnections();
        return state != null && !state.isEmpty() && state.get(state.size() - 1).isInitialized();
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        performActionInternal(event, "");
    }

    protected void performActionInternal(AnActionEvent event, String text) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        List<Connection> state = ServiceManager.getService(ConnectionHolder.class).getState().getConnections();
        try {
            String path = Messages.showInputDialog(project, "Enter request path", "Request Path",
                Messages.getQuestionIcon(), "/_search", new NonEmptyInputValidator());
            if(path == null) {
                return;
            }
            HTTPMethod method = getHttpMethod(project);
            if(method == null) {
                return;
            }

            Content content = new ElasticsearchRequestSender(text, state.get(state.size() - 1).getUrl(), path,
                method).getContent();

            ElasticsearchResponseRenderer.instance(project,
                content == null ? "No content were returned." : content.asString()).displayResponse();
        } catch(Throwable ex) {
            Messages.showMessageDialog(project, ex.getMessage(), "Error", Messages.getErrorIcon());
        }
    }

    @Nullable
    private HTTPMethod getHttpMethod(Project project) {
        int methodOrdinal = Messages.showChooseDialog(project, "Enter request method",
            "Request Method", Messages.getQuestionIcon(),
            Arrays.stream(HTTPMethod.values()).map(Enum::name).toArray(String[]::new), "POST");
        if(methodOrdinal == -1) {
            return null;
        }

        Optional<HTTPMethod> first = Arrays.stream(HTTPMethod.values())
            .filter(i -> i.ordinal() == methodOrdinal)
            .findFirst();
        HTTPMethod method = first.orElse(HTTPMethod.POST);
        return method;
    }

}
