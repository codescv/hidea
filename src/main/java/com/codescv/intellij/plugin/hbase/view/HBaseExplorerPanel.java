package com.codescv.intellij.plugin.hbase.view;

import com.codescv.intellij.plugin.hbase.HBasePluginConfiguration;
import com.codescv.intellij.plugin.hbase.logic.HBaseManager;
import com.codescv.intellij.plugin.hbase.model.HBaseServer;
import com.codescv.intellij.plugin.hbase.model.HBaseServerConfiguration;
import com.codescv.intellij.plugin.hbase.model.HBaseTable;
import com.codescv.intellij.plugin.hbase.utils.GuiUtils;
import com.codescv.intellij.plugin.hbase.view.editor.HBaseFileSystem;
import com.codescv.intellij.plugin.hbase.view.editor.HBaseObjectFile;
import com.intellij.icons.AllIcons;
import com.intellij.ide.CommonActionsManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.DialogWrapperDialog;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * explorer panel (main tool window ui)
 */
public class HBaseExplorerPanel extends JPanel implements Disposable {
    private JPanel rootPanel;
    private JPanel toolBarPanel;
    private JPanel treePanel;

    private Tree serverTree;
    private Project project;

    public HBaseExplorerPanel(Project project) {
        this.project = project;

        // database server trees
        serverTree = createTree();
        //serverTree.setCellRenderer(new MongoTreeRenderer());
        serverTree.setName("serverTree");

        JBScrollPane treeScrollPane = new JBScrollPane(serverTree);
        treePanel.setLayout(new BorderLayout());
        treePanel.add(treeScrollPane, BorderLayout.CENTER);

        // add root panel to Explorer panel
        setLayout(new BorderLayout());
        add(rootPanel, BorderLayout.CENTER);

        ApplicationManager.getApplication().invokeLater(this::reloadAllServers);

        DefaultActionGroup actionGroup = new DefaultActionGroup("HBaseExplorerGroup", false);

        actionGroup.add(new AnAction(AllIcons.General.Add) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                //System.out.println("add server");
                addServerConfiguration();
            }
        });

        actionGroup.add(new AnAction(AllIcons.General.Remove) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                DefaultMutableTreeNode selectedServerNode = getSelectedServerNode();
                if (selectedServerNode == null)
                    return;
                int result = JOptionPane.showConfirmDialog(toolBarPanel,
                                                           "confirm deleting server?",
                                                           UIManager.getString("OptionPane.titleText"),
                                                           JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    HBaseServer server = (HBaseServer) selectedServerNode.getUserObject();
                    HBaseServerConfiguration serverConfiguration = server.getConfig();
                    HBasePluginConfiguration pluginConfiguration = HBasePluginConfiguration.getInstance(HBaseExplorerPanel.this.project);
                    pluginConfiguration.removeServerConfig(serverConfiguration);
                    reloadAllServers();
                } else {
                    System.out.println("cancelled");
                }
            }
        });

        toolBarPanel.setLayout(new BorderLayout());
        GuiUtils.installActionGroupInToolBar(actionGroup, toolBarPanel, ActionManager.getInstance(), "HBaseExplorerActions", true);

    }

    private void addServerConfiguration() {
        HBaseServerConfiguration configuration = HBaseServerConfiguration.defaultConfiguration();
        HBaseServerConfigDialog dialog = new HBaseServerConfigDialog(configuration, this.project);
        dialog.setTitle("Add a HBase Server");
        dialog.setLocationRelativeTo(toolBarPanel);
        //dialog.setLocation(10, 80);
        dialog.pack();
        dialog.setVisible(true);

        if (dialog.isOK()) {
            System.out.println("save configuration!");
            HBasePluginConfiguration.getInstance(this.project).getServerConfigurations().add(configuration);
            // apply configuration and refresh UI
            HBaseWindowManager.getInstance(this.project).apply();
            System.out.println("xxx");
        } else {
            System.out.println("cancel");
        }
    }

    private Tree createTree() {

        Tree tree = new Tree() {

            private final JLabel myLabel = new JLabel(
                    "<html><center>No server available<br><br>You may use the add button to add configuration</center></html>"
            );

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!HBasePluginConfiguration.getInstance(HBaseExplorerPanel.this.project).getServerConfigurations().isEmpty()) return;

                myLabel.setFont(getFont());
                myLabel.setBackground(getBackground());
                myLabel.setForeground(getForeground());
                Rectangle bounds = getBounds();
                Dimension size = myLabel.getPreferredSize();
                myLabel.setBounds(0, 0, size.width, size.height);

                int x = (bounds.width - size.width) / 2;
                Graphics g2 = g.create(bounds.x + x, bounds.y + 20, bounds.width, bounds.height);
                try {
                    myLabel.paint(g2);
                } finally {
                    g2.dispose();
                }
            }
        };

        tree.getEmptyText().clear();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        return tree;
    }

    private void reloadAllServers() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {

            final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();

            HBaseManager manager = HBaseManager.getInstance(this.project);
            HBasePluginConfiguration configuration = HBasePluginConfiguration.getInstance(this.project);
            manager.reloadServers(configuration);

            for (HBaseServer server : manager.getServers()) {
                final DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server);
                for (HBaseTable table : server.getTables()) {
                    serverNode.add(new DefaultMutableTreeNode(table));
                }
                rootNode.add(serverNode);
            }

            ApplicationManager.getApplication().invokeLater(() -> {
                serverTree.setRootVisible(false);
                serverTree.invalidate();
                serverTree.setModel(new DefaultTreeModel(rootNode));
                serverTree.revalidate();
            });

        });
    }

    public void apply() {
        reloadAllServers();
    }

    public void installActions() {
        serverTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (!(mouseEvent.getSource() instanceof JTree)) {
                    return;
                }

                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) serverTree.getLastSelectedPathComponent();
                if (treeNode == null) {
                    return;
                }

                if (mouseEvent.getClickCount() == 2) {
                    if (treeNode.getUserObject() instanceof HBaseTable) {
                        openViewerForSelectedTable();
                    }
                }
            }
        });
    }

    private void openViewerForSelectedTable() {
        HBaseTable table = getSelectedTable();
        System.out.println("view table: " + table.getName());
        HBaseFileSystem.getInstance().openEditor(new HBaseObjectFile(project, getConfiguration(), getSelectedTable()));
    }

    public HBaseServerConfiguration getConfiguration() {
        DefaultMutableTreeNode serverNode = getSelectedServerNode();
        if (serverNode == null) {
            return null;
        }

        return ((HBaseServer) serverNode.getUserObject()).getConfig();
    }

    public HBaseTable getSelectedTable() {
        DefaultMutableTreeNode collectionNode = getSelectedTableNode();
        if (collectionNode == null) {
            return null;
        }

        return (HBaseTable) collectionNode.getUserObject();
    }

    private DefaultMutableTreeNode getSelectedTableNode() {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) serverTree.getLastSelectedPathComponent();
        if (treeNode != null) {
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof HBaseTable) {
                return treeNode;
            }
        }
        return null;
    }

    public DefaultMutableTreeNode getSelectedServerNode() {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) serverTree.getLastSelectedPathComponent();
        if (treeNode != null) {
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof HBaseTable) {
                return (DefaultMutableTreeNode) treeNode.getParent();
            }

            if (userObject instanceof HBaseServer) {
                return treeNode;
            }
        }
        return null;
    }

    @Override
    public void dispose() {
        System.out.println("disposed!");
        serverTree = null;
    }
}
