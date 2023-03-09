package cn.wanghw.utils;

import java.util.HashMap;

public class HashMapUtils {
    public static String dumpString(HashMap<String, String> hashMap) {
        return dumpString(hashMap, true);
    }

    public static String dumpString(HashMap<String, String> hashMap, boolean oneline) {
        return dumpString(hashMap, oneline, true, false);
    }

    public static String dumpString(HashMap<String, String> hashMap, boolean oneline, boolean newLine, boolean ignoreNull) {
        Object[] allKey = hashMap.keySet().toArray();
        String result = "";
        for (int i = 0; i < allKey.length; i++) {
            String key = allKey[i].toString();
            if (ignoreNull && (hashMap.get(key) == null || hashMap.get(key).equals(""))) continue;
            result += key + " = " + hashMap.get(key) + (oneline ? (i + 1 == allKey.length ? "" : ", ") : "\r\n");
        }
        if (result.endsWith(", "))
            result = result.substring(0, result.length() - 2);
        return !result.equals("") && oneline && newLine ? result + "\r\n" : result;
    }

}
