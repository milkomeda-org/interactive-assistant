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

package com.lauvinson.source.open.assistant

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.lauvinson.source.open.assistant.Constant.AbilityType_EXE
import com.lauvinson.source.open.assistant.Constant.AbilityType_EXE_PATH
import com.lauvinson.source.open.assistant.configuration.Config
import com.lauvinson.source.open.assistant.configuration.ConfigService
import com.lauvinson.source.open.assistant.utils.HttpUtils
import com.lauvinson.source.open.assistant.utils.JsonUtils
import org.apache.http.util.TextUtils
import java.awt.Color
import java.nio.charset.StandardCharsets
import java.util.*


/**
 * @License Apache2
 * @author created by vinson on 2019/6/28
 */
open class Group : ActionGroup() {

    private val config: Config? = ConfigService.getInstance().state

    companion object {
        val list: ArrayList<ApiAction> = ArrayList()
        var modify = true
        var virtualFile : VirtualFile? = null
        fun modify(){
            modify = true
        }
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        virtualFile = e?.getData(PlatformDataKeys.VIRTUAL_FILE)
        if (modify) {
            println("get menus")
            list.clear()
            if (config?.group?.isNotEmpty() == true) {
                for (m in config.group) {
                    list.add(ApiAction(m.key, m.value))
                }
            }
            modify = false
        }
        return list.toArray(arrayOf())
    }

    class ApiAction(name: String, private val mapping: java.util.LinkedHashMap<String, String>) : AnAction(name) {

        override fun actionPerformed(e: AnActionEvent) {
            if (Constant.AbilityType_API == mapping[Constant.AbilityType]) {
                mapping["url"]?.let {
                    val mEditor = e.getData(PlatformDataKeys.EDITOR) ?: return
                    val model = mEditor.selectionModel
                    val selectedText = model.selectedText
                    if (TextUtils.isEmpty(selectedText)) {
                        return
                    }
                    val response = JsonUtils.JsonFormart(mapping["url"]?.let { HttpUtils.URLGet(it, HashMap(), StandardCharsets.UTF_8.displayName()) }!!)
                    showPopupBalloon(mEditor, response)
                }
            }else if (AbilityType_EXE == mapping[Constant.AbilityType]) {
                mapping[AbilityType_EXE_PATH]?.let {
                    val sb = StringBuilder(mapping[AbilityType_EXE_PATH])
                    for (entry in mapping.entries) {
                        if (Constant.AbilityType == entry.key || Constant.AbilityType_EXE_PATH == entry.key) {
                            continue
                        }
                        if (Constant.AbilityType_FILE_ARGS_NAME == entry.key) {
                            sb.append(" -${entry.value}=${virtualFile?.path.toString()}")
                        }else {
                            sb.append(" -${entry.key}=${entry.value}")
                        }
                    }
                    println(sb.toString())
//                    Runtime.getRuntime().exec(sb.toString())
                    // 存在打开的项目则使用打开的项目，否则使用默认项目
                    // 存在打开的项目则使用打开的项目，否则使用默认项目
                    val projectManager = ProjectManager.getInstance()
                    val openProjects: Array<Project> = projectManager.openProjects
                    // 项目对象
                    // 项目对象
                    var project = if (openProjects.size > 0) openProjects[0] else projectManager.defaultProject
                    val runner = ShTerminalRunner(project)
                    runner.run(sb.toString(), "~", virtualFile?.name.toString())
                    println()
                }
            }
        }

        private fun showPopupBalloon(editor: Editor, result: String) {
            ApplicationManager.getApplication().invokeLater {
                val factory = JBPopupFactory.getInstance()
                factory.createHtmlTextBalloonBuilder(result, null, JBColor(Color(186, 238, 186), Color(73, 117, 73)), null)
                        .setFadeoutTime(60000)
                        .createBalloon()
                        .show(factory.guessBestPopupLocation(editor), Balloon.Position.below)
            }
        }
    }
}