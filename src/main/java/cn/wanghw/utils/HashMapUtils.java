package cn.wanghw.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HashMapUtils {
    public static String dumpString(HashMap<String, String> hashMap) {
        return dumpString(hashMap, true);
    }

    public static String dumpString(HashMap<String, String> hashMap, boolean oneline) {
        Object[] allKey = (Object[]) hashMap.keySet().toArray();
        String result = "";
        for (int i = 0; i < allKey.length; i++) {
            String key = allKey[i].toString();
            result += key + " = " + hashMap.get(key) + (oneline ? (i + 1 == allKey.length ? "" : ", ") : "\r\n");
        }
        return !result.equals("") && oneline ? result + "\r\n" : result;
    }
}
