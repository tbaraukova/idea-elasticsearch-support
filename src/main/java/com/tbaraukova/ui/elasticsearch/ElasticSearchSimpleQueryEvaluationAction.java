package com.tbaraukova.ui.elasticsearch;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NonEmptyInputValidator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.net.HTTPMethod;
import com.tbaraukova.ui.elasticsearch.connections.Connection;
import com.tbaraukova.ui.elasticsearch.connections.ConnectionHolder;
import com.tbaraukova.ui.elasticsearch.connections.Connections;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.util.Objects;

public class ElasticSearchSimpleQueryEvaluationAction extends AnAction {

    private static final String ELASTICSEARCH_QUERY_RESPONSE_JSON = "elasticsearch-response.json";

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
            int methodOrdinal = Messages.showChooseDialog(project, "Enter request method",
                "Request Method", Messages.getQuestionIcon(),
                Arrays.stream(HTTPMethod.values()).map(Enum::name).toArray(String[]::new), "POST");
            if(methodOrdinal == -1) {
                return;
            }

            Optional<HTTPMethod> first = Arrays.stream(HTTPMethod.values())
                .filter(i -> i.ordinal() == methodOrdinal)
                .findFirst();
            HTTPMethod method = first.orElse(HTTPMethod.POST);

            String uri = state.get(state.size() - 1).getUrl() + path;
            Messages.showMessageDialog(project, "Evaluate " + (StringUtils.isNotBlank(text) ? text + " " : "")
                + "on " + uri, "Information", Messages.getInformationIcon());
            Request request = getRequest(method, uri);
            if(StringUtils.isNotBlank(text)) {
                request.bodyString(text, ContentType.APPLICATION_JSON);
            }
            Content content = request.execute().returnContent();
            String response = content == null ? "No content were returned." : content.asString();

            VirtualFile file = ScratchFileService.getInstance().findFile(ScratchRootType.getInstance(),
                ELASTICSEARCH_QUERY_RESPONSE_JSON, ScratchFileService.Option.create_if_missing);

            Document responseDocument = FileDocumentManager.getInstance().getDocument(file);
            ApplicationManager.getApplication().runWriteAction(() -> responseDocument.setText(response));

            FileEditorManager.getInstance(project).openFile(file, false);

            ReformatCodeProcessor reformatCodeProcessor = new ReformatCodeProcessor(
                Objects.requireNonNull(PsiManager.getInstance(project).findFile(file)), false);
            reformatCodeProcessor.run();
        } catch(Throwable ex) {
            Messages.showMessageDialog(project, ex.getMessage(), "Error", Messages.getErrorIcon());
        }
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
