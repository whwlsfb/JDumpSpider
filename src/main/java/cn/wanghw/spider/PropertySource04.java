package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.OQLSnippets;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertySource04 implements ISpider {

    @Override
    public String getName() {
        return "ConsulPropertySources";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        long currentObjId = 0;
        try {
            List<Long> listObjId = new ArrayList<>();
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery(OQLSnippets.getTable + OQLSnippets.isMap + "map(filter(map(filter(map(heap.objects('org.springframework.cloud.consul.config.ConsulPropertySource'), 'it.properties'), 'isMap(it)'), 'getTable(it)'), 'it != null'), 'it.id');", o -> {
                if (o instanceof Long) {
                    listObjId.add((Long) o);
                }
                return false;
            });
            List<String> seenKeys = new ArrayList<>();
            for (Long objId : listObjId) {
                currentObjId = objId;
                oqlEngine.executeQuery(OQLSnippets.getValue + "map(filter(map(heap.findObject(" + objId.toString() + "), 'it'), 'it != null'), \"{'key': getValue(it.key),'value':getValue(it.value)}\")", o -> {
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
            if (result[0].equals("") && ex.getMessage().contains("is not found!")) {
                result[0] = "not found!\r\n";
            } else {
                System.out.println(ex + " objId: " + currentObjId);
            }
        }
        return result[0];
    }
}
