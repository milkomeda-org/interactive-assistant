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
