package com.lauvinson.open.assistant.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import javafx.util.Pair;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Setting implements Configurable {

    private static final String DISPLAY_NAME = "Interactive Assistant";
    private static Vector<Pair<Object, Map<String, String>>> group = new Vector<>();

    public Setting() {
        initComponents();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
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
            Map<String, String> value = new HashMap<String, String>(1){{
                put(String.valueOf(RandomUtils.nextInt(0, 9999)), String.valueOf(RandomUtils.nextInt(0, 9999)));
            }};
            group.add(new Pair<>("new", value));
            updateUi();
        });

        groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(groupList.getSelectedIndex() != -1) {
                    int selectIndex = groupList.locationToIndex(e.getPoint());
                    if (1 == e.getClickCount()) {
                        // show map
                        updateAbilityUi(selectIndex);
                    }
                    if(e.getClickCount() == 2) {
                        String newValue = doubleClick(groupList.getSelectedValue());
                        group.setElementAt(new Pair<>(newValue, group.get(selectIndex).getValue()), selectIndex);
                        updateUi();
                    }
                }
            }
        });
    }

    private void updateUi() {
        Object[] groups = group.stream().map(Pair::getKey).toArray();
        this.groupList.setListData(groups);
        groupList.updateUI();
    }

    private void updateAbilityUi(int groupIndex) {
        Pair<Object, Map<String, String>> abilitys = group.get(groupIndex);
        String[] abilityKeys = abilitys.getValue().keySet().toArray(new String[]{});
        abilityMapList.setListData(abilityKeys);
        abilityMapList.updateUI();
    }

    private String doubleClick(Object value) {
        return JOptionPane.showInputDialog(
                root,
                "Modify the group name",
                value
        );
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        createUIComponents();

        root = new JPanel();
        groupLabel = new JLabel();
        abilityMapLabel = new JLabel();
        groupPanel = new JScrollPane();
        groupList = new JList();
        abilityMapList = new JList();
        mapPanel = new JScrollPane();
        JPanel panel1 = new JPanel();
        addButton = new JButton();
        removeButton = new JButton();
        Spacer vSpacer1 = new Spacer();
        JPanel panel2 = new JPanel();

        //======== root ========
        {
            root.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));

            //---- groupLabel ----
            groupLabel.setText("Group");
            root.add(groupLabel, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- abilityMapLabel ----
            abilityMapLabel.setText("Ability Map");
            root.add(abilityMapLabel, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //======== groupPanel ========
            {

                //---- groupList ----
                groupList.setSelectionMode(2);
                groupPanel.setViewportView(groupList);
            }
            root.add(groupPanel, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null));

            //======== mapPanel ========
            {
                mapPanel.setViewportView(abilityMapList);
            }
            root.add(mapPanel, new GridConstraints(3, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null));

            //======== panel1 ========
            {
                panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));

                //---- addButton ----
                addButton.setDoubleBuffered(false);
                addButton.setFocusCycleRoot(false);
                addButton.setFocusTraversalPolicyProvider(false);
                addButton.setHideActionText(false);
                addButton.setHorizontalTextPosition(0);
                addButton.setIcon(new ImageIcon(getClass().getResource("/general/add.png")));
                addButton.setText("");
                addButton.setVerticalAlignment(0);
                addButton.putClientProperty("hideActionText", false);
                addButton.putClientProperty("html.disable", false);
                panel1.add(addButton, new GridConstraints(0, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));

                //---- removeButton ----
                removeButton.setEnabled(false);
                removeButton.setIcon(new ImageIcon(getClass().getResource("/general/remove.png")));
                removeButton.setText("");
                panel1.add(removeButton, new GridConstraints(1, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                    GridConstraints.SIZEPOLICY_FIXED,
                    null, null, null));
                panel1.add(vSpacer1, new GridConstraints(2, 0, 1, 1,
                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL,
                    GridConstraints.SIZEPOLICY_CAN_SHRINK,
                    GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                    null, null, null));
            }
            root.add(panel1, new GridConstraints(1, 1, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

            //======== panel2 ========
            {
                panel2.setLayout(new BorderLayout());
            }
            root.add(panel2, new GridConstraints(3, 1, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel root;
    private JLabel groupLabel;
    private JLabel abilityMapLabel;
    private JScrollPane groupPanel;
    private JList<Object> groupList;
    private JScrollPane mapPanel;
    private JList<String> abilityMapList;
    private JButton addButton;
    private JButton removeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
