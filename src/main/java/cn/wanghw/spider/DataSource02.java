package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.HashMap;

public class DataSource02 implements ISpider {
    @Override
    public String getName() {
        return "WeblogicDataSourceConnectionPoolConfig";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select {'url':x.url.toString(), 'driver':x.driver.toString(), 'name':x.name ? x.name.toString() : \"\", 'username':x.defaultConnectionInfo.username ? x.defaultConnectionInfo.username.toString():\"\", 'password':x.defaultConnectionInfo.p ? x.defaultConnectionInfo.p.toString():\"\" } from weblogic.jdbc.common.internal.DataSourceConnectionPoolConfig x", o -> {
                if (o instanceof HashMap) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) o;
                    result[0] += HashMapUtils.dumpString(hashMap, false) + "\r\n";
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
