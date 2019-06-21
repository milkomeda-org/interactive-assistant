package com.lauvinson.open.assistant.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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
    private JButton removeButton;

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
            Vector<JTextField> dataVector = new Vector<>();
            ListModel listDataModel = this.groupList.getModel();
            for (int i = 0; i < listDataModel.getSize(); i++) {
                JTextField s = (JTextField) listDataModel.getElementAt(i);
                dataVector.add(s);
            }
            JTextField textField = new JTextField(5);
            dataVector.add(textField);
            this.groupList.setListData(dataVector);
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
