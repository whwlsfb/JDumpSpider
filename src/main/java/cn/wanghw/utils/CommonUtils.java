package cn.wanghw.utils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLException;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CommonUtils {
    public static Long getObjectId(Object o) {
        if (o instanceof Long) {
            return (Long) o;
        } else if (o instanceof Double) {
            return ((Double) o).longValue();
        } else {
            return null;
        }
    }

    public static String dumpHashMaps(OQLEngine oqlEngine, List<Long> listObjId) throws OQLException {
        StringBuilder result = new StringBuilder();
        List<String> seenKeys = new ArrayList<>();
        for (Long objId : listObjId) {
            oqlEngine.executeQuery(OQLSnippets.getValue + "map(filter(map(heap.findObject(" + objId.toString() + "), 'it'), 'it != null && it.key'), \"{'key':getValue(it.key),'value':getValue(it.value)}\")", o -> {
                if (o instanceof HashMap) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) o;
                    String key = hashMap.get("key");
                    if (!seenKeys.contains(key)) {
                        result.append(hashMap.get("key")).append(" = ").append(hashMap.get("value")).append("\r\n");
                        seenKeys.add(hashMap.get("key"));
                    }
                }
                return false;
            });
        }
        return result.toString();
    }

    public static BigInteger getBigInteger(Object obj) {
        if (obj instanceof ScriptObjectMirror) {
            ScriptObjectMirror som = (ScriptObjectMirror) obj;
            int signum = -1;
            ArrayList<Integer> mags = new ArrayList<>();
            if (som.hasMember("signum")) {
                signum = (Integer) som.get("signum");
            }
            if (som.hasMember("mag") && som.get("mag") instanceof ScriptObjectMirror) {
                ScriptObjectMirror magSom = (ScriptObjectMirror) som.get("mag");
                for (int i = 0; i < magSom.keySet().size(); i++) {
                    mags.add(Integer.parseInt(magSom.get(String.valueOf(i)).toString()));
                }
            }
            if (!mags.isEmpty()) {
                try {
                    Constructor<BigInteger> constructor = BigInteger.class.getDeclaredConstructor(int[].class, int.class);
                    constructor.setAccessible(true);
                    return constructor.newInstance(mags.stream().mapToInt(i -> i).toArray(), signum);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
