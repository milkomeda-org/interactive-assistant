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

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.lauvinson.source.open.assistant.states.Runtime


/**
 * @License Apache2
 * @since 0.0.1
 * ability executor
 * @author created by vinson on 2019/6/28
 */
open class EditorMenu : ActionGroup() {

    companion object {
        var virtualFile : VirtualFile? = null
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val list = Runtime.getGroups()
        if (list.size < 1) {
            Runtime.flushGroups()
        }
        virtualFile = e?.getData(PlatformDataKeys.VIRTUAL_FILE)
        return list.toArray(arrayOf())
    }
}