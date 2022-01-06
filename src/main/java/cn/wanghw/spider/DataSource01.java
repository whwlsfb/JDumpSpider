package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;
import cn.wanghw.utils.OQLSnippets;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.HashMap;

public class DataSource01 implements ISpider {
    @Override
    public String getName() {
        return "SpringDataSourceProperties";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery(OQLSnippets.getValue + "map(filter(heap.objects('org.springframework.boot.autoconfigure.jdbc.DataSourceProperties'), 'it!=null && it.driverClassName != null'), \"{'username':getValue(it.username),'password':getValue(it.password),'url':getValue(it.url),'driverClassName':getValue(it.driverClassName)}\")", o -> {
                if (o instanceof HashMap) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) o;
                    result[0] += HashMapUtils.dumpString(hashMap, false);
                }
                return false;
            });
        } catch (Exception ex) {
            if (result[0].equals("") && ex.getMessage().contains("is not found!")) {
                result[0] = "not found!\r\n";
            } else {
                System.out.println(ex);
            }
        }
        return result[0];
    }
}
