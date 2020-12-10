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

package com.lauvinson.source.open.assistant.utils

import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import org.apache.commons.lang.StringUtils

object JsonUtils {

    private fun getLevelStr(level: Int): String {
        val levelStr = StringBuilder()
        for (levelI in 0 until level) {
            levelStr.append("\t")
        }
        return levelStr.toString()
    }

    fun format(s: String): String {
        var level = 0
        //存放格式化的json字符串
        val jsonForMatStr = StringBuilder()
        for (element in s) {
            if (level > 0 && '\n' == jsonForMatStr[jsonForMatStr.length - 1]) {
                jsonForMatStr.append(getLevelStr(level))
            }
            when (element) {
                '{', '[' -> {
                    jsonForMatStr.append(element).append("\n")
                    level++
                }
                ',' -> jsonForMatStr.append(element).append("\n")
                '}', ']' -> {
                    jsonForMatStr.append("\n")
                    level--
                    jsonForMatStr.append(getLevelStr(level))
                    jsonForMatStr.append(element)
                }
                else -> jsonForMatStr.append(element)
            }
        }
        return jsonForMatStr.toString()
    }


    fun isJson(json: String): Boolean {
        return if (StringUtils.isBlank(json)) {
            false
        } else try {
            JsonParser.parseString(json)
            true
        } catch (e: JsonSyntaxException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}
