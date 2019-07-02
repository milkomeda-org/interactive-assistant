package com.lauvinson.open.assistant.utils

import java.util.*

class CollectionUtils : org.apache.commons.collections.CollectionUtils() {
    companion object {

        fun getMapKeyValue(map: Map<String, String>?): Array<Array<String?>>? {
            var `object`: Array<Array<String?>>? = null
            if (map != null && map.isNotEmpty()) {
                val size = map.size
                `object` = Array(size) { arrayOfNulls<String>(2) }
                val iterator = map.entries.iterator()
                for (i in 0 until size) {
                    val entry = iterator.next()
                    val key = entry.key
                    val value = entry.value
                    `object`[i][0] = key
                    `object`[i][1] = value
                }
            }
            return `object`
        }

        fun mapCopy(paramsMap: HashMap<String, HashMap<String, String>>?, target: HashMap<String, HashMap<String, String>>?) {
            var resultMap = target
            if (resultMap == null) {
                resultMap = HashMap(paramsMap!!.size)
            }
            if (paramsMap == null) {
                return
            }
            var secon: HashMap<String, String>
            for (o in paramsMap.entries) {
                val entry = o as Map.Entry<*, *>
                val key = entry.key as String
                secon = HashMap(paramsMap[key]?.size ?: 0)
                paramsMap[key]?.let { secon.putAll(it) }
                resultMap[key] = secon
            }
        }
    }

}
