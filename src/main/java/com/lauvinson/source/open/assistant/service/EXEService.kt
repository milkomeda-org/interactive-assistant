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

package com.lauvinson.source.open.assistant.service

import com.intellij.openapi.project.Project
import com.lauvinson.source.open.assistant.actions.EditorMenu
import com.lauvinson.source.open.assistant.o.Constant
import com.lauvinson.source.open.assistant.utils.ShTerminalRunner
import java.util.LinkedHashMap

object EXEService {
    fun execute(project: Project, mapping: LinkedHashMap<String, String>) {
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