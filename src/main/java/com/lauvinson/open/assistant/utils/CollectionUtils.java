package com.lauvinson.open.assistant.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

    public static Object[][] getMapKeyValue(Map map) {
        Object[][] object = null;
        if ((map != null) && (!map.isEmpty())) {
            int size = map.size();
            object = new Object[size][2];
            Iterator iterator = map.entrySet().iterator();
            for (int i = 0; i < size; i++) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                object[i][0] = key;
                object[i][1] = value;
            }
        }
        return object;
    }

    public static void mapCopy(HashMap<String, HashMap<String, String>> paramsMap, HashMap<String, HashMap<String, String>> resultMap) {
        if (resultMap == null) {
            resultMap = new HashMap<>(paramsMap.size());
        }
        if (paramsMap == null) {
            return;
        }
        HashMap secon;
        for (Object o : paramsMap.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = (String) entry.getKey();
            secon = new HashMap<>(paramsMap.get(key).size());
            secon.putAll(paramsMap.get(key));
            resultMap.put(key, secon);
        }
    }
}
