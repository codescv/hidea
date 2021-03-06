package com.codescv.intellij.plugin.hbase.view;

import com.codescv.intellij.plugin.hbase.logic.HBaseManager;
import com.codescv.intellij.plugin.hbase.model.HBaseServerConfiguration;
import com.codescv.intellij.plugin.hbase.model.HBaseTable;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;

/**
 * Table querying/editing window (opened after double-clicking the table)
 */
public class HBaseEditorPanel extends JPanel implements Disposable {

    private JPanel rootPanel;
    private JPanel queryPanel;
    private JPanel resultPanel;
    private JTextField rowTextField;
    private JButton updateButton;
    private JTextField limitTextField;
    private JLabel rowLabel;

    private Tree resultTree;

    HBaseServerConfiguration configuration;
    HBaseTable table;
    HBaseManager hBaseManager;

    public HBaseEditorPanel(Project project, HBaseManager hBaseManager, HBaseServerConfiguration configuration, HBaseTable table) {

        this.configuration = configuration;
        this.table = table;
        this.hBaseManager = hBaseManager;

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showResults();
            }
        });

        rowLabel.setText("row:");

        resultTree = createTree();
        resultTree.setName("resultTree");
        JBScrollPane scrollPane = new JBScrollPane(resultTree);
        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(rootPanel, BorderLayout.CENTER);
    }

    public void showResults() {
        System.out.println("show results");
        String rowPrefix = rowTextField.getText();

        // default limit
        int limit = 50;

        try {
            limit = Integer.parseInt(limitTextField.getText());
        } catch (NumberFormatException e) {
            // ignore if empty
        }

        final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        ResultScanner results = this.hBaseManager.scan(this.configuration, this.table.getName(), rowPrefix);

        int counter = 0;
        for (Result r : results) {
            DefaultMutableTreeNode rowNode = new DefaultMutableTreeNode();
            String row = new String(r.getRow());
            rowNode.setUserObject(row);
            // System.out.println("row:" + row);
            for (Cell cell : r.listCells()) {
                String family = new String(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                String qualifier = new String(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());

                byte[] valueArray = cell.getValueArray();

                boolean isVisibleString = true;
                for (int i = cell.getValueOffset(); i < cell.getValueOffset() + cell.getValueLength(); i++) {
                    byte b = valueArray[i];
                    if (b <= 0) {
                        isVisibleString = false;
                        break;
                    }
                }

                String value;
                if (isVisibleString) {
                    value = new String(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                } else {
                    StringBuilder valueBuilder = new StringBuilder();
                    for (int i = cell.getValueOffset(); i < cell.getValueOffset() + cell.getValueLength(); i++) {
                        byte b = valueArray[i];
                        valueBuilder.append(String.format("\\x%02x", b & 0xFF));
                    }
                    value = valueBuilder.toString();
                }

                String data = family + ":" + qualifier + "->" + value;
                DefaultMutableTreeNode rowDataNode = new DefaultMutableTreeNode(data);
                rowNode.add(rowDataNode);
            }

            rootNode.add(rowNode);

            if (++counter >= limit)
                break;
        }

        System.out.println("rootnode:" + rootNode);

        resultTree.setRootVisible(false);
        resultTree.invalidate();
        resultTree.setModel(new DefaultTreeModel(rootNode));
        resultTree.validate();
    }

    private Tree createTree() {
        Tree tree = new Tree() {};
        tree.getEmptyText().clear();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        return tree;
    }

    public JComponent getResultPanel() {
        return resultPanel;
    }

    @Override
    public void dispose() {

    }
}
