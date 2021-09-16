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

package com.lauvinson.source.open.assistant.states

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.lauvinson.source.open.assistant.actions.Executor
import com.lauvinson.source.open.assistant.o.Constant

open class Runtime {

    companion object {
        private val configService = ApplicationManager.getApplication().getService(ConfigService::class.java)
        private val list: ArrayList<Executor> = ArrayList()

        fun flushGroups(){
            list.clear()
            val group = configService.state?.group
            group?.forEach { m ->
                val type = m.value[Constant.AbilityType]
                if (type?.equals(Constant.AbilityType_API) == true) {
                    list.add(Executor(m.key, m.value, AllIcons.General.Web))
                }else {
                    list.add(Executor(m.key, m.value, AllIcons.Actions.Execute))
                }
            }
        }

        fun getGroups(): ArrayList<Executor> {
            return list
        }
    }
}