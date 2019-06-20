package com.lauvinson.open.assistant.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Setting implements Configurable {

    private static final String displayName = "Interactive Assistant";
    private JPanel root;
    private JButton addButton;
    private JScrollPane mapPanel;
    private JScrollPane groupPanel;
    private JLabel abilityMapLabel;
    private JLabel groupLabel;
    private JList groupList;
    private JList abilityMapList;

    @Nls
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        this.loadSettings();
        return this.root;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        //set config
    }

    private void loadSettings() {
        //load xml file to the panel
        this.initListener();
    }

    private void initListener() {
        addButton.addActionListener(e -> {
            Vector<String> dataVector = new Vector<>();
            ListModel listDataModel = this.groupList.getModel();
            for (int i = 0; i < listDataModel.getSize(); i++) {
                String s = (String) listDataModel.getElementAt(i);
                dataVector.add(s);
            }
            dataVector.add("new");
            this.groupList.setListData(dataVector);
        });
    }
}
