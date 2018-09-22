package com.tbaraukova.ui.elasticsearch;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import java.io.IOException;
import java.util.Objects;

public class ElasticsearchResponseRenderer {

    private static final String ELASTICSEARCH_QUERY_RESPONSE_JSON = "elasticsearch-response.json";
    private final Project project;
    private final String response;

    private ElasticsearchResponseRenderer(Project project, String response) {
        this.project = project;
        this.response = response;
    }

    public static ElasticsearchResponseRenderer instance(Project project, String response) {
        return new ElasticsearchResponseRenderer(project, response);
    }

    public void displayResponse() throws IOException {
        VirtualFile file = ScratchFileService.getInstance().findFile(ScratchRootType.getInstance(),
            ELASTICSEARCH_QUERY_RESPONSE_JSON, ScratchFileService.Option.create_if_missing);

        Document responseDocument = FileDocumentManager.getInstance().getDocument(file);
        ApplicationManager.getApplication().runWriteAction(() -> responseDocument.setText(response));

        ReformatCodeProcessor reformatCodeProcessor = new ReformatCodeProcessor(
            Objects.requireNonNull(PsiManager.getInstance(project).findFile(file)), false);
        reformatCodeProcessor.setPostRunnable(() -> FileEditorManager.getInstance(project).openFile(file, false));
        reformatCodeProcessor.run();

    }
}
