package com.tbaraukova.ui.elasticsearch;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NonEmptyInputValidator;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.net.HTTPMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.util.Objects;

import static com.tbaraukova.ui.elasticsearch.ElasticsearchConnectorAction.ELASTICSEARCH_QUERY_JSON;

public class ElasticSearchQueryEvaluationAction extends AnAction {

    private static final ElasticsearchConnector ELASTICSEARCH_CONNECTOR = ElasticsearchConnector.INSTANCE;
    public static final String ELASTICSEARCH_QUERY_RESPONSE_JSON = "elasticsearch-response.json";

    @Override
    public void update(AnActionEvent e) {
        //Get required data keys
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        //Set visibility only in case of existing project and editor and if some text in the editor is selected
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        e.getPresentation().setVisible(project != null && editor != null &&
                editor.getSelectionModel().hasSelection() && virtualFile != null &&
                ELASTICSEARCH_QUERY_JSON.equals(virtualFile.getName()));
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        try {
            String path = Messages.showInputDialog(project, "Enter request path", "Request Path", Messages.getQuestionIcon(), "/_search", new NonEmptyInputValidator());
            HTTPMethod method = HTTPMethod.valueOf(Messages.showInputDialog(project, "Enter request method",
                    "Request Method", Messages.getQuestionIcon(), "POST", new NonEmptyInputValidator()));
            String text = getQueryToEvaluate(event);
            String uri = ELASTICSEARCH_CONNECTOR.getConnectionUrl() + path;
            Messages.showMessageDialog(project, "Evaluate " + text + " on " + uri, "Information", Messages.getInformationIcon());
            Request request = getRequest(method, uri);
            if (StringUtils.isNotBlank(text)) {
                request.bodyString(text, ContentType.APPLICATION_JSON);
            }
            Content content = request.execute().returnContent();
            String response = content == null ? "No content were returned." : content.asString();
            Messages.showMessageDialog(project, response, "Information", Messages.getInformationIcon());

            VirtualFile file = ScratchFileService.getInstance().findFile(ScratchRootType.getInstance(),
                    ELASTICSEARCH_QUERY_RESPONSE_JSON, ScratchFileService.Option.create_if_missing);

            Document responseDocument = FileDocumentManager.getInstance().getDocument(file);
//            responseDocument.setReadOnly(false);
            responseDocument.setText(response);

            FileEditorManager.getInstance(project).openFile(file, true);
            ReformatCodeProcessor reformatCodeProcessor = new ReformatCodeProcessor(Objects.requireNonNull(PsiManager.getInstance(project).findFile(file)), false);
//            reformatCodeProcessor.setPostRunnable(() -> responseDocument.setReadOnly(true));
            reformatCodeProcessor.run();
        } catch (Throwable ex) {
            Messages.showMessageDialog(project, ex.getMessage(), "Error", Messages.getErrorIcon());
        }
    }

    private String getQueryToEvaluate(AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        final Document document = editor == null ? null : editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();
        final int start = selectionModel.getSelectionStart();
        final int end = selectionModel.getSelectionEnd();
        return document == null ? null : document.getText(new TextRange(start, end));
    }

    private Request getRequest(HTTPMethod method, String uri) {
        switch (method) {
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
