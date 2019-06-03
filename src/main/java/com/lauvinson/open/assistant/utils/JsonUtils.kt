package com.lauvinson.open.assistant.utils

object JsonUtils {

    private fun getLevelStr(level: Int): String {
        val levelStr = StringBuilder()
        for (levelI in 0 until level) {
            levelStr.append("\t")
        }
        return levelStr.toString()
    }

    fun JsonFormart(s: String): String {
        var level = 0
        //存放格式化的json字符串
        val jsonForMatStr = StringBuilder()
        for (index in 0 until s.length) {
            val c = s[index]
            if (level > 0 && '\n' == jsonForMatStr[jsonForMatStr.length - 1]) {
                jsonForMatStr.append(getLevelStr(level))
            }
            when (c) {
                '{', '[' -> {
                    jsonForMatStr.append(c).append("\n")
                    level++
                }
                ',' -> jsonForMatStr.append(c).append("\n")
                '}', ']' -> {
                    jsonForMatStr.append("\n")
                    level--
                    jsonForMatStr.append(getLevelStr(level))
                    jsonForMatStr.append(c)
                }
                else -> jsonForMatStr.append(c)
            }
        }
        return jsonForMatStr.toString()
    }
}
