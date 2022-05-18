package cn.wanghw.utils;

import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLException;

import java.util.ArrayList;
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
}
