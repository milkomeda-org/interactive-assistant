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

package com.lauvinson.source.open.assistant.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.lauvinson.source.open.assistant.o.Constant
import com.lauvinson.source.open.assistant.utils.HttpClientUtils
import com.lauvinson.source.open.assistant.utils.JsonUtils
import com.lauvinson.source.open.assistant.utils.ShTerminalRunner
import org.apache.commons.lang.StringUtils
import org.apache.http.util.TextUtils
import java.awt.Color
import java.util.*

open class Executor(name: String, private val mapping: LinkedHashMap<String, String>) : AnAction(name) {

    override fun actionPerformed(e: AnActionEvent) {
        if (Constant.AbilityType_API == mapping[Constant.AbilityType]) {
            mapping[Constant.Ability_URL]?.let {
                if (StringUtils.isBlank(mapping[Constant.Ability_URL_ARGS_NAME])) {
                    return
                }
                val mEditor = e.getData(PlatformDataKeys.EDITOR) ?: return
                val model = mEditor.selectionModel
                val selectedText = model.selectedText
                if (TextUtils.isEmpty(selectedText)) {
                    return
                }
                val params = HashMap<String, String>()
                params[mapping[Constant.Ability_URL_ARGS_NAME].toString()] = selectedText.toString();
                val response = JsonUtils.format(mapping[Constant.Ability_URL]?.let { HttpClientUtils.get(it, params) }!!)
                showPopupBalloon(mEditor, response)
            }
        }else if (Constant.AbilityType_EXE == mapping[Constant.AbilityType]) {
            mapping[Constant.Ability_EXE_PATH]?.let {
                val sb = StringBuilder(mapping[Constant.Ability_EXE_PATH])
                for (entry in mapping.entries) {
                    if (Constant.AbilityType == entry.key || Constant.Ability_EXE_PATH == entry.key) {
                        continue
                    }
                    if (Constant.Ability_FILE_ARGS_NAME == entry.key) {
                        sb.append(" -${entry.value}=${EditorMenu.virtualFile?.path.toString()}")
                    } else {
                        sb.append(" -${entry.key}=${entry.value}")
                    }
                }
                val projectManager = ProjectManager.getInstance()
                val openProjects: Array<Project> = projectManager.openProjects
                // 项目对象
                // 项目对象
                val project = if (openProjects.isNotEmpty()) openProjects[0] else projectManager.defaultProject
                val runner = ShTerminalRunner(project)
                runner.run(sb.toString(), "~", EditorMenu.virtualFile?.name.toString())
            }
        }
    }

    private fun showPopupBalloon(editor: Editor, result: String) {
        ApplicationManager.getApplication().invokeLater {
            val factory = JBPopupFactory.getInstance()
            factory.createHtmlTextBalloonBuilder(result, null, JBColor(Color(186, 238, 186), Color(59, 63, 65)), null)
                .setFadeoutTime(60000)
                .createBalloon()
                .show(factory.guessBestPopupLocation(editor), Balloon.Position.below)
        }
    }
}