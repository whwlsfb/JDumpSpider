package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.HashMap;

public class OSS01 implements ISpider {
    @Override
    public String getName() {
        return "OSS";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select {'name': x.key.toString(), 'value': x.value.toString()} from java.util.Hashtable$Entry x where x.key.toString().toLowerCase().contains('oss.') && (x.key.toString().toLowerCase().contains('keyid') || x.key.toString().toLowerCase().contains('keysecret') || x.key.toString().toLowerCase().contains('bucketname') || x.key.toString().toLowerCase().contains('endpoint'))", o -> {
                if (o instanceof HashMap) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) o;
                    result[0] += HashMapUtils.dumpString(hashMap);
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