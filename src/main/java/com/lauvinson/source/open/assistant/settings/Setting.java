package com.lauvinson.source.open.assistant.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.json.JsonLanguage;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.options.Configurable;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.LanguageTextField;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import com.lauvinson.source.open.assistant.Constant;
import com.lauvinson.source.open.assistant.Group;
import com.lauvinson.source.open.assistant.configuration.Config;
import com.lauvinson.source.open.assistant.configuration.ConfigService;
import com.lauvinson.source.open.assistant.utils.CollectionUtils;
import com.lauvinson.source.open.assistant.utils.JsonUtils;
import io.grpc.internal.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.UUID;

public class Setting implements Configurable {

    private final ConfigService configService = ServiceManager.getService(ConfigService.class);
    private final Config config = configService.getState();
    private static final Object[][] EMPTY_TWO_DIMENSION_ARRAY = new Object[0][0];
    public static final LinkedHashMap<String, String> EMPTY_MAP = new LinkedHashMap<>();
    private final LinkedHashMap<String, LinkedHashMap<String, String>> group = new LinkedHashMap<>();
    private LinkedHashMap<String, String> attribute = new LinkedHashMap<>();

    private String selectGroupKey = "";

    private static final String DISPLAY_NAME = "Interactive Assistant";

    private JPanel root;
    private JList<Object> groupList;
    private AbilityTable attributeTable;
    private EditorTextField attributeMap;

    @Nls
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        CollectionUtils.Companion.mapCopy(config.getGroup(), this.group);

        //build panel
        //root
        this.root = new JPanel(new BorderLayout());

        JTabbedPane tabs;
        {
            //tabs
            tabs = new JBTabbedPane();
            {
                //power
                JPanel power = new JPanel(new GridLayout(1,2,10,10));
                {
                    //top
                    JPanel top = new JPanel(new BorderLayout());
                    {
                        //groupLIst
                        CollectionListModel<Object> groupListModel = new CollectionListModel<>();
                        this.groupList = new JBList<>(groupListModel);
                        ToolbarDecorator groupListDecorator = ToolbarDecorator.createDecorator(this.groupList);
                        groupListDecorator.setAddAction(anActionButton -> {
                            LinkedHashMap<String, String> value = new LinkedHashMap<>(1) {{
                                put(Constant.AbilityType, Constant.AbilityType_API);
                            }};
                            group.put(UUID.randomUUID().toString(), value);
                            updateGroupUi();
                        });
                        groupListDecorator.setRemoveAction(anActionButton -> {
                            group.remove(selectGroupKey);
                            updateAbilityUi(EMPTY_TWO_DIMENSION_ARRAY);
                            setAttributeMapData(EMPTY_MAP);
                            updateGroupUi();
                        });
                        top.add(groupListDecorator.createPanel());
                    }
                    //bottom
                    JPanel bottom = new JPanel(new BorderLayout());
                    {
                        //tab
                        JTabbedPane attributeTab = new JBTabbedPane();
                        attributeTab.setAutoscrolls(true);
                        //attributes
                        this.attributeTable = new AbilityTable(new AbilityTableModel());
                        this.attributeTable.setRowSelectionAllowed(false);
                        this.attributeTable.setCellSelectionEnabled(true);
                        ToolbarDecorator attributeMapDecorator = ToolbarDecorator.createDecorator(this.attributeTable);
                        attributeMapDecorator.setRemoveActionUpdater(e -> attributeTable.getSelectedRow() > -1 && attributeTable.getRowCount() > attributeTable.getSelectedRow() && !Constant.AbilityType.equals(attributeTable.getValueAt(attributeTable.getSelectedRow(), 0).toString()));
                        attributeMapDecorator.setAddActionUpdater(e -> !"".equals(selectGroupKey));
                        attributeMapDecorator.setAddAction(anActionButton -> {
                            LinkedHashMap<String, String> attributes = group.get(selectGroupKey);
                            attributes.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
                            Object[][] ability = CollectionUtils.Companion.getMapKeyValue(attributes);
                            updateAbilityUi(ability);
                            setAttributeMapData(attributes);
                        });
                        attributeMapDecorator.setRemoveAction(anActionButton -> {
                            int row = attributeTable.getSelectedRow();
                            attribute.remove(attributeTable.getValueAt(row, 0).toString());
                            Object[][] ability = CollectionUtils.Companion.getMapKeyValue(attribute);
                            updateAbilityUi(ability);
                            setAttributeMapData(attribute);
                        });
                        attributeTab.addTab("Table", attributeMapDecorator.createPanel());
                        this.attributeMap = new LanguageTextField(JsonLanguage.INSTANCE, null, "");
                        this.attributeMap.setOneLineMode(false);
                        JScrollPane attributeMapScroll = new JBScrollPane(this.attributeMap);
                        attributeMapScroll.setPreferredSize(power.getPreferredSize());
                        attributeTab.addTab("Map", attributeMapScroll);
                        bottom.add(attributeTab);
                    }
                    power.add(top);
                    power.add(bottom);
                }
                tabs.addTab("Power", power);
            }
            this.root.add(tabs);
        }

        // fill data
        this.loadSettings();
        tabs.setSelectedIndex(0);
        return this.root;
    }

    @Override
    public boolean isModified() {
        boolean modify = config.getGroup().equals(group);
        Group.Companion.reset(group);
        return !modify;
    }

    @Override
    public void apply() {
        //set config
        LinkedHashMap<String, LinkedHashMap<String, String>> temp = new LinkedHashMap<>(this.group.size());
        CollectionUtils.Companion.mapCopy(this.group, temp);
        config.setGroup(temp);
    }

    @Override
    public void reset() {
        group.clear();
        CollectionUtils.Companion.mapCopy(config.getGroup(), group);
        updateGroupUi();
        updateAbilityUi(EMPTY_TWO_DIMENSION_ARRAY);
        setAttributeMapData(EMPTY_MAP);
    }

    private void loadSettings() {
        //load xml file to the panel
        this.updateGroupUi();
        this.initListener();
    }

    private void initListener() {

        groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (groupList.getSelectedIndex() != -1) {
                    Object oldValue = groupList.getSelectedValue();
                    if (1 == e.getClickCount()) {
                        selectGroupKey = oldValue.toString();
                        // show map
                        attribute = group.get(selectGroupKey);
                        Object[][] ability = CollectionUtils.Companion.getMapKeyValue(attribute);
                        updateAbilityUi(ability);
                        setAttributeMapData(attribute);
                    }
                    if (e.getClickCount() == 2) {
                        String newValue = doubleClick(oldValue);
                        if (StringUtils.isNotBlank(newValue)) {
                            group.put(newValue, group.remove(oldValue.toString()));
                            updateGroupUi();
                        }
                    }
                }
            }
        });

        attributeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (attributeTable.getSelectedRow() != -1) {
                    attributeTable.editCellAt(attributeTable.getSelectedRow(), attributeTable.getSelectedColumn(), e);
                }
            }
        });

        attributeMap.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                if (attributeMap.isFocusOwner()) {
                    String text = attributeMap.getText();
                    boolean isJson = JsonUtils.INSTANCE.isJson(text);
                    if (isJson) {
                        Type type = new TypeToken<LinkedHashMap<String, String>>() {
                        }.getType();
                        LinkedHashMap<String, String> attributes = group.get(selectGroupKey);
                        LinkedHashMap<String, String> map;
                        try {
                            map = new Gson().fromJson(JsonParser.parse(text).toString(), type);
                        } catch (IOException ex) {
                            return;
                        }
                        attributes.clear();
                        attributes.putAll(map);
                        Object[][] ability = CollectionUtils.Companion.getMapKeyValue(map);
                        updateAbilityUi(ability);
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

    private void updateAbilityUi(Object[][] data) {
        ((AbilityTableModel) this.attributeTable.getModel()).setData(data);
        this.groupList.updateUI();
        this.attributeTable.updateUI();
    }

    private void changeAbilityKey(String oldk, String newk) {
        String oldv = group.get(selectGroupKey).remove(oldk);
        group.get(selectGroupKey).put(newk, oldv);
        setAttributeMapData(group.get(selectGroupKey));
    }

    private void changeAbilityValue(String key, String value) {
        group.get(selectGroupKey).put(key, value);
        setAttributeMapData(group.get(selectGroupKey));
    }

    private String doubleClick(Object value) {
//        return new Messages.InputDialog("New name", "Rename", Messages.getQuestionIcon(), value.toString(), new NonEmptyInputValidator()).getInputString();
        return JOptionPane.showInputDialog(
                root,
                "Modify the group name",
                value
        );
    }

    private void setAttributeMapData(LinkedHashMap<String, String> mapData) {
        attributeMap.setText(new GsonBuilder().setPrettyPrinting().create().toJson(new LinkedHashMap<>(mapData)));
    }


    class AbilityTableModel extends AbstractTableModel {
        // 表格中第一行所要显示的内容存放在字符串数组columnNames中
        String[] abilityColumnNames = {"Name", "Pass"};
        // 表格中各行的内容保存在二维数组data中
        Object[][] data;

        // 下述方法是重写AbstractTableModel中的方法，其主要用途是被JTable对象调用，以便在表格中正确的显示出来。程序员必须根据采用的数据类型加以恰当实现。


        AbilityTableModel() {
        }

        public void setData(Object[][] data) {
            this.data = data;
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
            return null != data ? data.length : 0;
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
            if (Constant.AbilityType.equals(value.toString())) {
                JOptionPane.showMessageDialog(root, "The \""
                        + value.toString()
                        + "\" has already.");
                return;
            }
            if (data[0][col] instanceof Integer && !(value instanceof Integer)) {
                try {
                    data[row][col] = Integer.valueOf(value.toString());
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


    class AbilityTable extends JBTable {

        public AbilityTable(TableModel model) {
            super(model);
        }

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            if (Constant.AbilityType.equals(attributeTable.getValueAt(row, 0).toString())) {
                if (column == 1) {
                    String[] values = new String[]{Constant.AbilityType_API, Constant.AbilityType_EXE};
                    return new DefaultCellEditor(new JComboBox<>(values));
                }
            }
            return super.getCellEditor(row, column);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return !(Constant.AbilityType.equals(attributeTable.getValueAt(row, 0).toString()) && column == 0);
        }
    }
}
