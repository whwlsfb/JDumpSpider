package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.HashMap;

public class Redis02 implements ISpider {
    @Override
    public String getName() {
        return "JedisClient";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select {'hostname':x.host.toString(),'port':x.port.toString(), 'password':x.password ? x.password.toString() : '', 'database':x.db.toString()} from redis.clients.jedis.Client x", o -> {
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