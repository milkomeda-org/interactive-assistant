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
import com.lauvinson.source.open.assistant.service.EXEService
import com.lauvinson.source.open.assistant.utils.HttpClientUtils
import com.lauvinson.source.open.assistant.utils.JsonUtils
import org.apache.commons.lang.StringUtils
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
            mapping[Constant.Ability_URL]?.let { it ->
                if (StringUtils.isBlank(mapping[Constant.Ability_URL_ARGS_NAME])) {
                    return
                }

                val params = HashMap<String, String>()
                for (entry in mapping.entries) {
                    if (Constant.Ability_URL_ARGS_NAME == entry.key) {
                        (e.getData(PlatformDataKeys.EDITOR)?.selectionModel?.selectedText).also { params[mapping[Constant.Ability_URL_ARGS_NAME].toString()] = it.toString() }
                    } else {
                        if (entry.key.startsWith(Constant.SYS_PREFIX)) {
                            continue
                        }
                        params[entry.key] = entry.value
                    }
                }
                val response = HttpClientUtils.get(HttpClientUtils.buildURI(it, params).toString())
                showPopupBalloon(this.name, response)
            }
        }else if (Constant.AbilityType_EXE == mapping[Constant.AbilityType]) {
           EXEService.execute(project, mapping)
        }
    }

    private fun showPopupBalloon(name: String, response: List<String>) {
        SampleDialogWrapper(name, response).showAndGet()
    }

    private inner class SampleDialogWrapper(private val _title: String, private val response: List<String>) : DialogWrapper(true) {
        override fun createCenterPanel(): JComponent {
            val screen = Toolkit.getDefaultToolkit().screenSize
            val size = Dimension(screen.width/3,screen.height/3)
            val panel = BorderLayoutPanel()
            var text = response[0].trim()
            val lang =
                when {
                    response[1].startsWith("application/json") -> {
                        text = text.let { JsonUtils.format(it) }
                        JsonLanguage.INSTANCE
                    }
                    response[1].startsWith("text/html") -> {
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