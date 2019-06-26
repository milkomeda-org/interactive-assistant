package com.lauvinson.open.assistant.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.table.JBTable;
import com.lauvinson.open.assistant.configuration.Config;
import com.lauvinson.open.assistant.configuration.ConfigService;
import com.lauvinson.open.assistant.utils.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.UUID;

public class Setting implements Configurable {

    private ConfigService configService = ServiceManager.getService(ConfigService.class);
    private Config config = configService.getState();
    private LinkedHashMap<String, LinkedHashMap<String, String>> group = new LinkedHashMap<String, LinkedHashMap<String, String>>() {{
        putAll(config.api);
    }};

    private static final String DISPLAY_NAME = "Interactive Assistant";

    private JPanel root;
    private JButton addButton;
    private JScrollPane groupPanel;
    private JLabel abilityMapLabel;
    private JLabel groupLabel;
    private JList<Object> groupList;
    private JButton removeButton;
    private JPanel top;
    private JTable mapTabel;


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
        return !config.api.equals(group);
    }

    @Override
    public void apply() throws ConfigurationException {
        //set config
        config.api = group;
    }

    private void loadSettings() {
        //load xml file to the panel
        this.updateGroupUi();
        this.initListener();
    }

    private void initListener() {
        addButton.addActionListener(e -> {
            LinkedHashMap<String, String> value = new LinkedHashMap<String, String>(1){{
                put(String.valueOf(RandomUtils.nextInt(0, 9999)), String.valueOf(RandomUtils.nextInt(0, 9999)));
            }};
            group.put(UUID.randomUUID().toString(), value);
            updateGroupUi();
        });

        groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(groupList.getSelectedIndex() != -1) {
                    Object oldValue = groupList.getSelectedValue();
                    if (1 == e.getClickCount()) {
                        // show map
                        updateAbilityUi(oldValue);
                    }
                    if(e.getClickCount() == 2) {
                        String newValue = doubleClick(oldValue);
                        if (StringUtils.isNotBlank(newValue)) {
                            group.put(newValue, group.remove(oldValue));
                            updateGroupUi();
                        }
                    }
                }
            }
        });
    }

    private void updateGroupUi() {
        Object[] groups = group.keySet().toArray();
        this.groupList.setListData(groups);
        this.groupList.updateUI();
    }

    private void updateAbilityUi(Object key) {
        LinkedHashMap<String, String> abilitys = group.get(key);
        Object[][] ability = CollectionUtils.getMapKeyValue(abilitys);
        this.mapTabel.setModel(new AbilityTableModel(ability));
        this.mapTabel.updateUI();
    }

    private void changeAbilityKey(String oldk, String newk) {
        Object select = groupList.getSelectedValue().toString();
        String oldv = group.get(select).remove(oldk);
        group.get(select).put(newk, oldv);
    }

    private void changeAbilityValue(String key, String value) {
        Object select = groupList.getSelectedValue().toString();
        group.get(select).put(key, value);
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
        mapTabel = new JBTable(new AbilityTableModel());
    }


    class AbilityTableModel extends AbstractTableModel {
        // 表格中第一行所要显示的内容存放在字符串数组columnNames中
        String[] abilityColumnNames = { "Name", "Pass"};
        // 表格中各行的内容保存在二维数组data中
        Object[][] data;

        // 下述方法是重写AbstractTableModel中的方法，其主要用途是被JTable对象调用，以便在表格中正确的显示出来。程序员必须根据采用的数据类型加以恰当实现。


        AbilityTableModel() {
        }

        AbilityTableModel(Object[][] data) {
            this.data = data;
        }

        // 获得列的数目
        @Override
        public int getColumnCount() {
            return abilityColumnNames.length;
        }

        // 获得行的数目
        @Override
        public int getRowCount() {
            return data.length;
        }

        // 获得某列的名字，而目前各列的名字保存在字符串数组columnNames中
        @Override
        public String getColumnName(int col) {
            return abilityColumnNames[col];
        }

        // 获得某行某列的数据，而数据保存在对象数组data中
        @Override
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        // 判断每个单元格的类型
        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        // 将表格声明为可编辑的
        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        // 改变某个数据的值
        @Override
        public void setValueAt(Object value, int row, int col) {
            if (data[0][col] instanceof Integer && !(value instanceof Integer)) {
                try {
                    data[row][col] = new Integer(value.toString());
                    fireTableCellUpdated(row, col);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(root, "The \""
                            + getColumnName(col)
                            + "\" column accepts only integer values.");
                }
            } else {
                switch (col) {
                    case 0:
                        changeAbilityKey(data[row][0].toString(), value.toString());
                        break;
                    case 1:
                        changeAbilityValue(data[row][0].toString(), value.toString());
                        break;
                    default:
                        break;
                }
                data[row][col] = value;
                fireTableCellUpdated(row, col);
            }

        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i = 0; i < numRows; i++) {
                System.out.print(" row " + i + ":");
                for (int j = 0; j < numCols; j++) {
                    System.out.print(" " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    }


}
