package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.OQLSnippets;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertySource01 implements ISpider {

    @Override
    public String getName() {
        return "OriginTrackedMapPropertySource";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            List<Long> listObjId = new ArrayList<>();
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select map(filter(map(x.source.m ? x.source.m.table : x.source.table, 'it'), 'it != null'),\"it.id\") from org.springframework.boot.env.OriginTrackedMapPropertySource x", o -> {
                if (o instanceof Long) {
                    listObjId.add((Long) o);
                }
                return false;
            });
            List<String> seenKeys = new ArrayList<>();
            for (Long objId : listObjId) {
                oqlEngine.executeQuery(OQLSnippets.getValue + "map(filter(map(heap.findObject(" + objId.toString() + "), 'it'), 'it != null'), \"{'key':it.key.value && it.key.value.toString(),'value':getValue(it.value)}\")", o -> {
                    if (o instanceof HashMap) {
                        HashMap<String, String> hashMap = (HashMap<String, String>) o;
                        String key = hashMap.get("key");
                        if (!seenKeys.contains(key)) {
                            result[0] += hashMap.get("key") + " = " + hashMap.get("value") + "\r\n";
                            seenKeys.add(hashMap.get("key"));
                        }
                    }
                    return false;
                });
            }
        } catch (Exception ex) {
            if (!result[0].equals("")) {
                result[0] = "not found!";
            }
        }
        return result[0];
    }
}
