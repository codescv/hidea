package com.codescv.intellij.plugin.hbase.view;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;

/**
 * window manager
 */
public class HBaseWindowManager {
    //private static final Icon MONGO_ICON = GuiUtils.loadIcon("mongo_logo.png");

    private static final String HBASE_EXPLORER = "HBase Explorer";

    private final Project project;
    private final HBaseExplorerPanel explorerPanel;

    public static HBaseWindowManager getInstance(Project project) {
        return ServiceManager.getService(project, HBaseWindowManager.class);
    }

    public HBaseWindowManager(Project project) {
        this.project = project;

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        explorerPanel = new HBaseExplorerPanel(project);
        explorerPanel.installActions();
        Content mongoExplorer = ContentFactory.SERVICE.getInstance().createContent(explorerPanel, null, false);

        ToolWindow toolMongoExplorerWindow = toolWindowManager.registerToolWindow(HBASE_EXPLORER, false, ToolWindowAnchor.RIGHT);
        toolMongoExplorerWindow.getContentManager().addContent(mongoExplorer);
        //toolMongoExplorerWindow.setIcon(MONGO_ICON);
    }

    public void unregisterMyself() {
        ToolWindowManager.getInstance(project).unregisterToolWindow(HBASE_EXPLORER);
    }

    public void apply() {
        ApplicationManager.getApplication().invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        explorerPanel.apply();
                    }
                });
    }
}
