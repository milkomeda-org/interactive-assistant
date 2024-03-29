/*
 * The MIT License (MIT)
 * Copyright © 2019 <copyright holders>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM,DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Equivalent description see [http://rem.mit-license.org/]
 */

package com.lauvinson.source.open.assistant.ui

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.intellij.json.JsonLanguage
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.*
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import com.lauvinson.source.open.assistant.o.Constant
import com.lauvinson.source.open.assistant.states.ConfigService
import com.lauvinson.source.open.assistant.states.Runtime
import com.lauvinson.source.open.assistant.utils.CollectionUtils.Companion.getMapKeyValue
import com.lauvinson.source.open.assistant.utils.CollectionUtils.Companion.mapCopy
import com.lauvinson.source.open.assistant.utils.JsonUtils.isJson
import com.lauvinson.source.open.assistant.utils.UiUtils
import org.apache.commons.lang3.StringUtils
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor
import org.jetbrains.annotations.Nls
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.IOException
import java.util.*
import javax.swing.*
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableModel


class Setting : Configurable {
    private val configService = ConfigService.getInstance()
    private val config = configService.state
    private var group = LinkedHashMap<String, LinkedHashMap<String, String>>()
    private var attribute: LinkedHashMap<String, String>? = LinkedHashMap()
    private var selectGroupKey = ""
    private var root: JPanel? = null
    private var groupList: JBList<Any>? = null
    private var attributeTable: AbilityTable? = null
    private var attributeTableModel: AbilityTableModel? = null
    private var attributeMap: EditorTextField? = null
    @Nls
    override fun getDisplayName(): String {
        return DISPLAY_NAME
    }

    override fun createComponent(): JComponent? {
        mapCopy(config!!.group, group)

        //build panel
        //root
        root = JPanel(BorderLayout())
        var tabs: JTabbedPane
        root.run {

            //tabs
            tabs = JBTabbedPane()
            tabs.run{
                //abilities
                val gbl = GridBagLayout()
                val abilities = JPanel(gbl)
                abilities.run{
                    //top
                    val top = JPanel(BorderLayout())
                    top.run{
                        //groupLIst
                        val groupListModel = CollectionListModel<Any>()
                        groupList = JBList(groupListModel)
                        val groupListDecorator = ToolbarDecorator.createDecorator(groupList as JBList<Any>)
                        groupListDecorator.disableUpDownActions()
                        groupListDecorator.setAddAction {
                            val value: LinkedHashMap<String, String> = object : LinkedHashMap<String, String>(1) {
                                init {
                                    put(Constant.AbilityType, Constant.AbilityType_API)
                                }
                            }
                            group[UUID.randomUUID().toString()] = value
                            flushGroupData()
                            (groupList as JBList<Any>).setSelectedIndex((groupList as JBList<Any>).lastVisibleIndex)
                        }
                        groupListDecorator.setRemoveAction {
                            group.remove(selectGroupKey)
                            clearAbilityUi()
                            flushGroupData()
                        }
                        top.add(groupListDecorator.createPanel())
                    }
                    //bottom
                    val bottom = JPanel(BorderLayout())
                    bottom.run{
                        //tab
                        val attributeTab: JTabbedPane = JBTabbedPane()
                        attributeTab.autoscrolls = true
                        //attributes
                        attributeTableModel = AbilityTableModel()
                        attributeTable = AbilityTable(attributeTableModel)
                        attributeTable!!.rowSelectionAllowed = false
                        attributeTable!!.cellSelectionEnabled = true
                        val attributeMapDecorator = ToolbarDecorator.createDecorator(
                            attributeTable!!
                        )
                        attributeMapDecorator.disableUpDownActions()
                        attributeMapDecorator.setRemoveActionUpdater {
                            attributeTable!!.selectedRow > -1 && attributeTable!!.rowCount > attributeTable!!.selectedRow && Constant.AbilityType != attributeTable!!.getValueAt(
                                attributeTable!!.selectedRow, 0
                            ).toString()
                        }
                        attributeMapDecorator.setAddActionUpdater { "" != selectGroupKey }
                        attributeMapDecorator.setAddAction {
                            val attributes = group[selectGroupKey]
                            attributes!![UUID.randomUUID().toString()] = UUID.randomUUID().toString()
                            val ability: Array<Array<Any?>>? = getMapKeyValue(attributes)
                            updateAbilityUi(ability)
                        }
                        attributeMapDecorator.setRemoveAction {
                            val row = attributeTable!!.selectedRow
                            attribute!!.remove(attributeTable!!.getValueAt(row, 0).toString())
                            val ability: Array<Array<Any?>>? = getMapKeyValue(attribute)
                            updateAbilityUi(ability)
                        }
                        attributeTab.addTab("Table", attributeMapDecorator.createPanel())
                        attributeMap = LanguageTextField(JsonLanguage.INSTANCE, null, "")
                        (attributeMap as LanguageTextField).setOneLineMode(false)
                        val attributeMapScroll: JScrollPane = JBScrollPane(attributeMap)
                        attributeMapScroll.preferredSize = abilities.preferredSize
                        attributeTab.addTab("Map", attributeMapScroll)
                        bottom.add(attributeTab)
                    }
                    val gbc = GridBagConstraints()
                    gbc.anchor = GridBagConstraints.FIRST_LINE_START
                    gbc.insets = Insets(5, 5, 5, 5)
                    UiUtils.addGridBagComp(abilities, top, gbc,  0, 0, 1, 1,
                        GridBagConstraints.BOTH, 0.3, 1.0)
                    UiUtils.addGridBagComp(abilities, bottom, gbc,  1, 0, 1, 1,
                        GridBagConstraints.BOTH, 0.7, 1.0)
                }
                tabs.addTab("Abilities", abilities)

            }
            root!!.add(tabs)
        }

        // fill data
        loadSettings()
        tabs.selectedIndex = 0
        return root
    }

    override fun isModified(): Boolean {
        val modify = config!!.group == group
        Runtime.flushGroups()
        return !modify
    }

    override fun apply() {
        //set config
        val temp = LinkedHashMap<String, LinkedHashMap<String, String>>(group.size)
        mapCopy(group, temp)
        config!!.group = temp
    }

    override fun reset() {
        group.clear()
        mapCopy(config!!.group, group)
        flushGroupData()
        clearAbilityUi()
    }

    private fun loadSettings() {
        //load xml file to the panel
        flushGroupData()
        initListener()
    }

    /**
     * initialize listener
     */
    private fun initListener() {
        groupList!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (groupList!!.selectedIndex != -1) {
                    if (e.clickCount == 2) {
                        val newValue = doubleClick(selectGroupKey)
                        if (StringUtils.isNotBlank(newValue)) {
                            group[newValue] = group.remove(selectGroupKey) as LinkedHashMap<String, String>
                            flushGroupData()
                            updateGroupUi()
                        }
                    }
                }
            }
        })
        groupList!!.addListSelectionListener {
            if (groupList!!.selectedIndex == -1) {
                clearAbilityUi()
                return@addListSelectionListener
            }
            selectGroupKey = groupList!!.selectedValue.toString()
            // show map
            attribute = group[selectGroupKey]
            val abilities: Array<Array<Any?>>? = getMapKeyValue(attribute)
            updateAbilityUi(abilities)
        }
        attributeTable!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                // attribute table cell to be editable
                if (e.clickCount == 2 && attributeTable!!.hasFocus() && attributeTable!!.selectedRow != -1) {
                    val row = attributeTable!!.selectedRow
                    val col = attributeTable!!.selectedColumn
                    if (attributeTable!!.isCellEditable(row, col)) {
                        attributeTable!!.editCellAt(row, col)
                    }
                }
            }
        })
        attributeTable!!.addPropertyChangeListener {
            flushAttributeMap()
        }
        attributeMap!!.document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                if (attributeMap!!.isFocusOwner) {
                    val text = attributeMap!!.text
                    val isJson = isJson(text)
                    if (isJson) {
                        val type = object : TypeToken<LinkedHashMap<String?, String?>?>() {}.type
                        val attributes = group[selectGroupKey]
                        val map: LinkedHashMap<String, String> = try {
                            Gson().fromJson(text, type)
                        } catch (ex: IOException) {
                            ex.printStackTrace()
                            return
                        }
                        attributes!!.clear()
                        attributes.putAll(map)
                        val abilities: Array<Array<Any?>>? = getMapKeyValue(map)
                        updateAbilityUi(abilities)
                    }
                }
            }
        })
    }

    /**
     * update the ui of group list panel
     */
    private fun updateGroupUi() {
        groupList!!.updateUI()
    }

    /**
     * reset the list data of group list panel
     */
    private fun flushGroupData() {
        val groups: Array<Any> = group.keys.toTypedArray()
        groupList!!.setListData(groups)
        updateGroupUi()
    }

    /**
     * update the ui of abilities panel
     * @param data table data
     */
    private fun updateAbilityUi(data: Array<Array<Any?>>?) {
        (attributeTable!!.model as AbilityTableModel).setData(data)
        groupList!!.updateUI()
        attributeTable!!.updateUI()
    }

    /**
     * release the data and clean the ui for abilities panel
     */
    private fun clearAbilityUi() {
        updateAbilityUi(EMPTY_TWO_DIMENSION_ARRAY)
    }

    /**
     * change key of the map data of abilities
     * @param oldKey before changing key
     * @param newKey after changed key
     */
    private fun changeAbilityKey(oldKey: String, newKey: String) {
        val oldValue = group[selectGroupKey]!!.remove(oldKey)
        group[selectGroupKey]!![newKey] = oldValue!!
    }

    /**
     * change the value of the map data of abilities
     * @param key map key
     * @param value new value
     */
    private fun changeAbilityValue(key: String, value: String) {
        group[selectGroupKey]!![key] = value
    }

    /**
     * get a string value from dialog
     * @param value the initial value for dialog
     * @return new string value
     */
    private fun doubleClick(value: Any): String {
        return ShowGet("Rename", value.toString()).get()()
    }

    /**
     * set map value to abilities map panel
     */
    private fun flushAttributeMap() {
        val mapData = if (null != group[selectGroupKey]) {
            group[selectGroupKey]
        } else {
            EMPTY_MAP
        }
        attributeMap!!.text = GsonBuilder().setPrettyPrinting().create()
            .toJson(LinkedHashMap(mapData))
    }

    /**
     * abilities table model extends #[AbstractTableModel]
     */
    internal inner class AbilityTableModel  // 下述方法是重写AbstractTableModel中的方法，其主要用途是被JTable对象调用，以便在表格中正确的显示出来。程序员必须根据采用的数据类型加以恰当实现。
        : AbstractTableModel() {
        // 表格中第一行所要显示的内容存放在字符串数组columnNames中
        private var abilitiesColumnNames = arrayOf("Key", "Value")

        // 表格中各行的内容保存在二维数组data中
        private var data: Array<Array<Any?>>? = null
        @JvmName("setData1")
        fun setData(d: Array<Array<Any?>>?) {
            data = d
        }

        // 获得列的数目
        override fun getColumnCount(): Int {
            return abilitiesColumnNames.size
        }

        // 获得行的数目
        override fun getRowCount(): Int {
            return if (null != data) data!!.size else 0
        }

        // 获得某列的名字，而目前各列的名字保存在字符串数组columnNames中
        override fun getColumnName(col: Int): String {
            return abilitiesColumnNames[col]
        }

        // 获得某行某列的数据，而数据保存在对象数组data中
        override fun getValueAt(row: Int, col: Int): Any? {
            return data!![row][col]
        }

        // 判断每个单元格的类型
        override fun getColumnClass(c: Int): Class<out Any> {
            val o = getValueAt(0, c)
            return o!!::class.java
        }

        // 将表格声明为可编辑的
        override fun isCellEditable(row: Int, col: Int): Boolean {
            return true
        }

        // 改变某个数据的值
        override fun setValueAt(value: Any, row: Int, col: Int) {
            if (Constant.AbilityType == value.toString()) {
                JOptionPane.showMessageDialog(
                    root, "The \""
                            + value.toString()
                            + "\" has already."
                )
                return
            }
            if (data!![0][col] is Int && value !is Int) {
                try {
                    data!![row][col] = Integer.valueOf(value.toString())
                    fireTableCellUpdated(row, col)
                } catch (e: NumberFormatException) {
                    JOptionPane.showMessageDialog(
                        root, "The \""
                                + getColumnName(col)
                                + "\" column accepts only integer values."
                    )
                }
            } else {
                when (col) {
                    0 -> changeAbilityKey(data!![row][0].toString(), value.toString())
                    1 -> changeAbilityValue(data!![row][0].toString(), value.toString())
                    else -> {
                    }
                }
                data!![row][col] = value
                fireTableCellUpdated(row, col)
            }
        }
    }

    /**
     * abilities table panel extends #[JBTable]
     */
    internal inner class AbilityTable(model: TableModel?) : JBTable(model) {
        override fun getCellEditor(row: Int, column: Int): TableCellEditor {
            if (Constant.AbilityType == attributeTable!!.getValueAt(row, 0).toString()) {
                if (column == 1) {
                    val values = arrayOf(Constant.AbilityType_API, Constant.AbilityType_EXE)
                    return ComboBoxCellEditor(ComboBox(values))
                }
            }
            return DefaultCellEditor(JBTextField())
        }

        override fun isCellEditable(row: Int, column: Int): Boolean {
            return !(Constant.AbilityType == attributeTable!!.getValueAt(row, 0).toString() && column == 0)
        }
    }

    companion object {
        private val EMPTY_TWO_DIMENSION_ARRAY = Array(0) { arrayOfNulls<Any>(0) }
        val EMPTY_MAP = LinkedHashMap<String, String>()
        private const val DISPLAY_NAME = "Interactive Assistant"
    }

    internal inner class ShowGet(_title: String, private val init: String) : DialogWrapper(true) {
        private val label = JTextField()
        override fun createCenterPanel(): JComponent {
            label.text = this.init
            return label
        }

        init {
            init()
            this.title = _title
        }

        fun get(): () -> String {
            return fun (): String {
                return if(this.showAndGet()){
                    this.label.text
                }else {
                    this.init
                }
            }
        }
    }
}