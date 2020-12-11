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

package com.lauvinson.source.open.assistant.o;

import java.util.LinkedHashMap;

public class Constant {
    public static final String SYS_PREFIX = "$";
    public static final String AbilityType = "$type";
    public static final String AbilityType_API = "api";
    public static final String AbilityType_EXE = "exe";
    public static final String Ability_URL = "$url";
    public static final String Ability_EXE_PATH = "$path";
    public static final String Ability_FILE_ARGS_NAME = "$args_name";
    public static final String Ability_URL_ARGS_NAME = "$args_name";

    public static final LinkedHashMap<String, LinkedHashMap<String, String>> AbilityExample = new LinkedHashMap<>(1) {{
        LinkedHashMap<String, String> yd = new LinkedHashMap<>(1) {{
            put(AbilityType, AbilityType_API);
            put(Ability_URL, "http://fanyi.youdao.com/translate?&doctype=json&type=AUTO");
            put(Ability_URL_ARGS_NAME, "i");
        }};
        put("有道翻译", yd);
    }};
}
