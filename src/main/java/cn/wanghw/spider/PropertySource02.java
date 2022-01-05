package cn.wanghw.spider;

import cn.wanghw.ISpider;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertySource02 implements ISpider {

    @Override
    public String getName() {
        return "MutablePropertySources";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            List<Long> listObjId = new ArrayList<>();
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select map(filter(map(filter(x.propertySourceList.array, \"it!=null\"), \"it.source.table\"), \"it!=null\"),\"it.id\") from org.springframework.core.env.MutablePropertySources x", o -> {
                if (o instanceof Long) {
                    listObjId.add((Long) o);
                }
                return false;
            });
            for (Long objId : listObjId) {
                oqlEngine.executeQuery("select map(filter(map(heap.findObject(" + objId.toString() + "), 'it'), 'it != null'), \"{'key':it.key.value && it.key.value.toString(),'value':it.value.value.toString()}\")", o -> {
                    if (o instanceof HashMap) {
                        HashMap<String, String> hashMap = (HashMap<String, String>) o;
                        result[0] += hashMap.get("key") + " = " + hashMap.get("value") + "\r\n";
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
