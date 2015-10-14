package com.codescv.intellij.plugin.hbase.view.editor;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.ex.FakeFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class HBaseFakeFileType extends FakeFileType {

    public static final Icon MONGO_ICON = null; //GuiUtils.loadIcon("mongo_logo.png");

    public static final FileType INSTANCE = new HBaseFakeFileType();


    @Override
    public Icon getIcon() {
        return MONGO_ICON;
    }

    @Override
    public boolean isMyFileType(VirtualFile file) {
        return false;
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "json";
    }

    @NotNull
    @Override
    public String getName() {
        return "HBASE";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "HBASE";
    }
}
