package com.lauvinson.open.assistant.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.lauvinson.open.assistant.utils.CollectionUtils;
import javafx.util.Pair;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Setting implements Configurable {

    private static final String DISPLAY_NAME = "Interactive Assistant";
    private static Vector<Pair<Object, Map<String, String>>> group = new Vector<>();

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
        this.groupList.updateUI();
    }

    private void updateAbilityUi(int groupIndex) {
        Pair<Object, Map<String, String>> abilitys = group.get(groupIndex);
        Object[][] ability = CollectionUtils.getMapKeyValue(abilitys.getValue());
        this.mapTabel.setModel(new AbilityTableModel(ability));
        this.mapTabel.updateUI();
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

            if (col < 2) {
                return false;
            } else {
                return true;
            }
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
