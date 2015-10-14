package com.codescv.intellij.plugin.hbase.view.editor;

import com.codescv.intellij.plugin.hbase.logic.HBaseManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class HBaseDataEditorProvider implements FileEditorProvider, ApplicationComponent, DumbAware {


    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file instanceof HBaseObjectFile;
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        HBaseObjectFile HBaseObjectFile = (HBaseObjectFile) file;
        return new HBaseDataEditor(project, HBaseManager.getInstance(project), HBaseObjectFile);
    }

    @Override
    public void disposeEditor(@NotNull FileEditor editor) {
        editor.dispose();
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public FileEditorState readState(@NotNull Element sourceElement, @NotNull Project project, @NotNull VirtualFile file) {
        return FileEditorState.INSTANCE;
    }

    @Override
    public void writeState(@NotNull FileEditorState state, @NotNull Project project, @NotNull Element targetElement) {

    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "MongoData";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "MongoPlugin.MongoEditorProvider";
    }
}
