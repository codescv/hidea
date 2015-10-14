package com.codescv.intellij.plugin.hbase.view.editor;

import com.codescv.intellij.plugin.hbase.model.HBaseServerConfiguration;
import com.codescv.intellij.plugin.hbase.model.HBaseTable;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.util.LocalTimeCounter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HBaseObjectFile extends VirtualFile {

    private final long myModStamp;
    private HBaseServerConfiguration configuration;
    private HBaseTable table;
    private Project project;
    private String name;

    public HBaseObjectFile(Project project, HBaseServerConfiguration configuration, HBaseTable table) {
        this.project = project;
        this.configuration = configuration;
        this.table = table;
        this.name = String.format("%s/%s", configuration.getName(), table.getName());
        this.myModStamp = LocalTimeCounter.currentTime();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    public FileType getFileType() {
        return HBaseFakeFileType.INSTANCE;
    }

    @NotNull
    @Override
    public VirtualFileSystem getFileSystem() {
        return HBaseFileSystem.getInstance();
    }

    @NotNull
    @Override
    public String getPath() {
        return name;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    public HBaseServerConfiguration getConfiguration() {
        return configuration;
    }

    public HBaseTable getTable() {
        return table;
    }

    public Project getProject() {
        return project;
    }

//    Unused methods
    @Override
    public VirtualFile getParent() {
        return null;
    }

    @Override
    public VirtualFile[] getChildren() {
        return new VirtualFile[0];
    }

    @NotNull
    @Override
    public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
        throw new UnsupportedOperationException("MongoResultFile is read-only");
    }

    @Override
    public long getModificationStamp() {
        return myModStamp;
    }

    @NotNull
    @Override
    public byte[] contentsToByteArray() throws IOException {
        return new byte[0];
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {

    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
