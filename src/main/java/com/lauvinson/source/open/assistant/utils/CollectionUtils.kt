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

import java.util.*

class CollectionUtils : org.apache.commons.collections.CollectionUtils() {
    companion object {

        fun getMapKeyValue(map: Map<String, String>?): Array<Array<Any?>>? {
            var `object`: Array<Array<Any?>>? = null
            if (map != null && map.isNotEmpty()) {
                val size = map.size
                `object` = Array(size) { arrayOfNulls<Any>(2) }
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

        fun mapCopy(paramsMap: LinkedHashMap<String, LinkedHashMap<String, String>>, target: LinkedHashMap<String, LinkedHashMap<String, String>>) {
            var secon: HashMap<String, String>
            for (o in paramsMap.entries) {
                val entry = o as Map.Entry<*, *>
                val key = entry.key as String
                secon = LinkedHashMap(paramsMap[key]?.size ?: 0)
                paramsMap[key]?.let { secon.putAll(it) }
                target[key] = secon
            }
        }
    }

}
