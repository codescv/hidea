package com.codescv.intellij.plugin.hbase.view;

import com.codescv.intellij.plugin.hbase.model.HBaseServerConfiguration;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.*;

public class HBaseServerConfigDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField serverTextField;
    private JTextField portTextField;
    private JTextField serverNameTextField;

    private HBaseServerConfiguration configuration;
    private Project project;

    private boolean isOK;

    public HBaseServerConfigDialog(HBaseServerConfiguration configuration, Project project) {
        //JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        //super(topFrame);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.project = project;

        // load config
        serverNameTextField.setText(configuration.getName());
        serverTextField.setText(configuration.getServer());
        portTextField.setText(configuration.getPort());
        this.configuration = configuration;
    }

    private void onOK() {
        // add your code here
        String host = serverTextField.getText();
        String port = portTextField.getText();
        String name = serverNameTextField.getText();

        // check port number
        // System.out.println("host:" + host + ", port:" + port);

        // save configuration
        this.configuration.setName(name);
        this.configuration.setServer(host);
        this.configuration.setPort(port);

        this.isOK = true;

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary

        this.isOK = false;

        dispose();
    }

    public boolean isOK() {
        return this.isOK;
    }
}
