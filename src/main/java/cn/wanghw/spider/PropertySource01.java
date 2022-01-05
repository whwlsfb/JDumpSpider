package cn.wanghw.spider;

import cn.wanghw.ISpider;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.HashMap;

public class PropertySource01 implements ISpider {

    @Override
    public String getName() {
        return "OriginTrackedMapPropertySource";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select map(filter(map(x.source.m.table, \"it\"), \"it != null\"),\"{'key':it.key.value.toString(),'value':it.value.value.value.toString()}\") from org.springframework.boot.env.OriginTrackedMapPropertySource x", o -> {
                if (o instanceof HashMap) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) o;
                    result[0] += hashMap.get("key") + " = " + hashMap.get("value") + "\r\n";
                }
                return false;
            });
        } catch (Exception ex) {
            if (!result[0].equals("")) {
                result[0] = "not found!";
            }
        }
        return result[0];
    }
}
