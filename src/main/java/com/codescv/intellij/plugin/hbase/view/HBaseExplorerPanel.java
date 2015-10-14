package com.codescv.intellij.plugin.hbase.view;

import com.codescv.intellij.plugin.hbase.logic.HBaseManager;
import com.codescv.intellij.plugin.hbase.model.HBaseServer;
import com.codescv.intellij.plugin.hbase.model.HBaseServerConfiguration;
import com.codescv.intellij.plugin.hbase.model.HBaseTable;
import com.codescv.intellij.plugin.hbase.view.editor.HBaseFileSystem;
import com.codescv.intellij.plugin.hbase.view.editor.HBaseObjectFile;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

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
    private JLabel titleLabel;
    private JPanel toolBarPanel;
    private JPanel treePanel;

    private Tree serverTree;
    private Project project;

    public HBaseExplorerPanel(Project project) {
        this.project = project;

        titleLabel.setText("ToolBar");

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
    }

    private Tree createTree() {

        Tree tree = new Tree() {

            private final JLabel myLabel = new JLabel(
                    String.format("<html><center>No Mongo server available<br><br>You may use <img src=\"%s\"> to add configuration</center></html>", "")
            );

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                //if (!getServerConfigurations().isEmpty()) return;
                if (true)
                    return;
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
            serverTree.setRootVisible(false);
            final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();

            HBaseManager manager = HBaseManager.getInstance(this.project);
            manager.reloadServers();
            for (HBaseServer server: manager.getServers()) {
                final DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server);
                for (HBaseTable table: server.getTables()) {
                    serverNode.add(new DefaultMutableTreeNode(table));
                }
                rootNode.add(serverNode);
            }

            serverTree.invalidate();
            serverTree.setModel(new DefaultTreeModel(rootNode));
            serverTree.revalidate();
        });
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
