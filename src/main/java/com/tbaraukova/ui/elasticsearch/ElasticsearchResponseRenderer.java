package com.tbaraukova.ui.elasticsearch;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.json.JsonLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldProvider;
import com.intellij.ui.content.impl.ContentImpl;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

public class ElasticsearchResponseRenderer {

    private static final String ELASTICSEARCH_QUERY_RESPONSE_JSON = "elasticsearch-response.json";
    public static final String TOOL_WINDOW_ID = "Elasticsearch response";
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

        PsiFile psiFile = Objects.requireNonNull(PsiManager.getInstance(project).findFile(file));
        ReformatCodeProcessor reformatCodeProcessor = new ReformatCodeProcessor(psiFile, false);
        reformatCodeProcessor.setPostRunnable(() -> createViewTab(psiFile, responseDocument));
        reformatCodeProcessor.run();

    }

    private void createViewTab(PsiElement element, Document responseDocument) {
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

        EditorTextField editorTextField = EditorTextFieldProvider.getInstance()
            .getEditorField(JsonLanguage.INSTANCE, project, Collections.emptyList());
        editorTextField.setDocument(responseDocument);

        ToolWindow toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
        if(toolWindow == null) {
            toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID,
                true,
                ToolWindowAnchor.BOTTOM);
        }

        ToolWindow finalToolWindow = toolWindow;
        toolWindow.activate(() -> {
            final String text = SymbolPresentationUtil.getSymbolPresentableText(element);
            final ContentImpl content = new ContentImpl(editorTextField, text, true);
            finalToolWindow.getContentManager().removeAllContents(false);
            finalToolWindow.getContentManager().addContent(content);
            finalToolWindow.getContentManager().setSelectedContent(content, true);
        });
    }
}
