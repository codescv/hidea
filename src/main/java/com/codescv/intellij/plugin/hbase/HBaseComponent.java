package com.codescv.intellij.plugin.hbase;

import com.codescv.intellij.plugin.hbase.view.HBaseWindowManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * hbase project component
 */
public class HBaseComponent extends AbstractProjectComponent {
    private static final String COMPONENT_NAME = "HBase";

    public HBaseComponent(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public void projectOpened() {
        HBaseWindowManager.getInstance(myProject);
    }

    @Override
    public void projectClosed() {
        HBaseWindowManager.getInstance(myProject).unregisterMyself();
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

}
