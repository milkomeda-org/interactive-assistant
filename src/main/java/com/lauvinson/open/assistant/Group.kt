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
import java.awt.Color

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
                for (u in m.value as LinkedHashMap<*, *>) {
                    list.add(ApiAction(m.key + ">" + u.key, (u.value ?: "") as String))
                }
            }
            init = true
        }
        return list.toArray(arrayOf())
    }

    class ApiAction(name: String, private val mapping: String) : AnAction(name) {

        override fun actionPerformed(e: AnActionEvent) {
            val mEditor = e.getData(PlatformDataKeys.EDITOR) ?: return
            showPopupBalloon(mEditor, mapping)
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
}