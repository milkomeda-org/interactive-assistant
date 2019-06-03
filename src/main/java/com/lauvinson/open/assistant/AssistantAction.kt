package com.lauvinson.open.assistant

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.lauvinson.open.assistant.utils.HttpUtils
import com.lauvinson.open.assistant.utils.JsonUtils
import org.apache.http.util.TextUtils

import java.awt.*
import java.nio.charset.StandardCharsets
import java.util.HashMap

/**
 * @author created by vinson on 2019/5/22
 */
class AssistantAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        // TODO: insert action logic here
        val mEditor = e.getData(PlatformDataKeys.EDITOR) ?: return
        val model = mEditor.selectionModel
        val selectedText = model.selectedText
        if (TextUtils.isEmpty(selectedText)) {
            return
        }
        val response = JsonUtils.JsonFormart(HttpUtils.URLGet("http://fanyi.youdao.com/openapi.do?keyfrom=neverland&key=969918857&type=data&doctype=json&version=1.1&q=" + selectedText!!, HashMap(), StandardCharsets.UTF_8.displayName())!!)
        println(response)
        showPopupBalloon(mEditor, response)
    }

    private fun showPopupBalloon(editor: Editor, result: String) {
        ApplicationManager.getApplication().invokeLater {
            val factory = JBPopupFactory.getInstance()
            factory.createHtmlTextBalloonBuilder(result, null, JBColor(Color(186, 238, 186), Color(73, 117, 73)), null)
                    .setFadeoutTime(5000)
                    .createBalloon()
                    .show(factory.guessBestPopupLocation(editor), Balloon.Position.below)
        }
    }
}
