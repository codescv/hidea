package com.codescv.intellij.plugin.hbase.view.editor;

import com.codescv.intellij.plugin.hbase.logic.HBaseManager;
import com.codescv.intellij.plugin.hbase.view.HBaseEditorPanel;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class HBaseDataEditor extends UserDataHolderBase implements FileEditor {
    private HBaseEditorPanel panel;
    private boolean disposed;

    public HBaseDataEditor(Project project, HBaseManager hBaseManager, HBaseObjectFile HBaseObjectFile) {
        panel = new HBaseEditorPanel(project, hBaseManager, HBaseObjectFile.getConfiguration(), HBaseObjectFile.getTable());
        ApplicationManager.getApplication().invokeLater(panel::showResults);
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        System.out.println("disposed:" + isDisposed());
        return isDisposed() ? new JPanel() : panel;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return panel == null ? null : panel.getResultPanel();
    }

    @NotNull
    @Override
    public String getName() {
        return "HBase Data";
    }

    @Override
    public void dispose() {
        if (!disposed) {
            panel.dispose();
            panel = null;
            disposed = true;
        }
    }

    public boolean isDisposed() {
        return disposed;
    }

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return FileEditorState.INSTANCE;
    }

//    Unused methods

    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void selectNotify() {

    }

    @Override
    public void deselectNotify() {

    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }
}
