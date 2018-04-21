package com.tbaraukova.ui.elasticsearch;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;

import static com.tbaraukova.ui.elasticsearch.ElasticsearchConnectorAction.ELASTICSEARCH_QUERY_JSON;

public class ElasticSearchQueryEvaluationAction extends ElasticSearchSimpleQueryEvaluationAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        String text = getQueryToEvaluate(event);
        performActionInternal(event, text);
    }

    @Override
    protected boolean isVisible(Project project, Editor editor, VirtualFile virtualFile) {
        return super.isVisible(project, editor, virtualFile) && project != null && editor != null &&
                editor.getSelectionModel().hasSelection() && virtualFile != null &&
                ELASTICSEARCH_QUERY_JSON.equals(virtualFile.getName());
    }

    private String getQueryToEvaluate(AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        final Document document = editor == null ? null : editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();
        final int start = selectionModel.getSelectionStart();
        final int end = selectionModel.getSelectionEnd();
        return document == null ? null : document.getText(new TextRange(start, end));
    }
}
