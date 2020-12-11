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

import com.intellij.json.JsonLanguage
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.LanguageTextField
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.components.BorderLayoutPanel
import com.lauvinson.source.open.assistant.o.Constant
import com.lauvinson.source.open.assistant.utils.HttpClientUtils
import com.lauvinson.source.open.assistant.utils.JsonUtils
import com.lauvinson.source.open.assistant.utils.OkHttpUtils
import com.lauvinson.source.open.assistant.utils.ShTerminalRunner
import okhttp3.Response
import org.apache.commons.lang.StringUtils
import org.apache.http.HttpHeaders
import org.apache.http.util.TextUtils
import java.awt.Dimension
import java.awt.Toolkit
import java.util.*
import javax.swing.Icon
import javax.swing.JComponent


open class Executor(private var name: String, private var mapping: LinkedHashMap<String, String>, icon: Icon) :
    AnAction(name, "", icon) {


    override fun actionPerformed(e: AnActionEvent) {
        val projectManager = ProjectManager.getInstance()
        val openProjects: Array<Project> = projectManager.openProjects
        val project = if (openProjects.isNotEmpty()) openProjects[0] else projectManager.defaultProject
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
                for (entry in mapping.entries) {
                    if (Constant.Ability_URL_ARGS_NAME == entry.key) {
                        params[mapping[Constant.Ability_URL_ARGS_NAME].toString()] = selectedText.toString()
                    } else {
                        if (entry.key.startsWith(Constant.SYS_PREFIX)) {
                            continue
                        }
                        params[entry.key] = entry.value
                    }
                }
                val response = OkHttpUtils.get(HttpClientUtils.buildURI(it, params).toString())
                showPopupBalloon(this.name, response)
            }
        }else if (Constant.AbilityType_EXE == mapping[Constant.AbilityType]) {
            mapping[Constant.Ability_EXE_PATH]?.let {
                val sb = StringBuilder(it)
                for (entry in mapping.entries) {
                    if (Constant.Ability_FILE_ARGS_NAME == entry.key) {
                        sb.append(" -${entry.value}=${EditorMenu.virtualFile?.path.toString()}")
                    } else {
                        if (entry.key.startsWith(Constant.SYS_PREFIX)) {
                            continue
                        }
                        sb.append(" -${entry.key}=${entry.value}")
                    }
                }
                val runner = ShTerminalRunner(project)
                runner.run(sb.toString(), "~", EditorMenu.virtualFile?.name.toString())
            }
        }
    }

    private fun showPopupBalloon(name: String, response: Response) {
        SampleDialogWrapper(name, response).showAndGet()
    }

    private inner class SampleDialogWrapper(private val _title: String, private val response: Response) : DialogWrapper(true) {
        override fun createCenterPanel(): JComponent {
            val screen = Toolkit.getDefaultToolkit().screenSize
            val size = Dimension(screen.width/3,screen.height/3)
            val panel = BorderLayoutPanel()
            var text = response.body().string().trim()
            val lang =
                when {
                    response.header(HttpHeaders.CONTENT_TYPE).startsWith("application/json") -> {
                        text = JsonUtils.format(text)
                        JsonLanguage.INSTANCE
                    }
                    response.header(HttpHeaders.CONTENT_TYPE).startsWith("text/html") -> {
                        HTMLLanguage.INSTANCE
                    }
                    else -> {
                        PlainTextLanguage.INSTANCE
                    }
                }
            val textPanel = LanguageTextField(lang, null, text)
            textPanel.setOneLineMode(false)
            val scroll = JBScrollPane()
            scroll.preferredSize = size
            scroll.setViewportView(textPanel)
            panel.add(scroll)
            return scroll
        }

        init {
            init()
            title = this._title
        }
    }

}