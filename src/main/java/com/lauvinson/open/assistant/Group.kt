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

package com.lauvinson.open.assistant

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.lauvinson.open.assistant.configuration.Config
import com.lauvinson.open.assistant.configuration.ConfigService
import com.lauvinson.open.assistant.utils.HttpUtils
import com.lauvinson.open.assistant.utils.JsonUtils
import org.apache.http.util.TextUtils
import java.awt.Color
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

/**
 * @License Apache2
 * @author created by vinson on 2019/6/28
 */
open class Group : ActionGroup() {

    private val config: Config? = ConfigService.getInstance().state

    companion object {
        val list: ArrayList<ApiAction> = ArrayList()
        var init: Boolean = false

        fun init() {
            list.clear()
            init = false
        }
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        if (!init && null != config && config.api.isNotEmpty()) {
            for (m in config.api) {
                for (u in m.value as HashMap<*, *>) {
                    u.key?.run {
                        u.value?.run { list.add(ApiAction(m.key + ">" + u.key, u.value.toString())) }
                    }
                }
            }
            init = true
        }
        return list.toArray(arrayOf())
    }

    class ApiAction(name: String, private val mapping: String) : AnAction(name) {

        override fun actionPerformed(e: AnActionEvent) {
            val mEditor = e.getData(PlatformDataKeys.EDITOR) ?: return
            val model = mEditor.selectionModel
            val selectedText = model.selectedText
            if (TextUtils.isEmpty(selectedText)) {
                return
            }
            val response = JsonUtils.JsonFormart(HttpUtils.URLGet(mapping, HashMap(), StandardCharsets.UTF_8.displayName())!!)
            showPopupBalloon(mEditor, response)
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