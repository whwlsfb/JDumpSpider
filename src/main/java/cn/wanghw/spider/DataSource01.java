package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;
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
            oqlEngine.executeQuery("select {'username':x.username.value.toString(),'password':x.password.value.toString(),'url':x.url.value.toString(),'driverClassName':x.driverClassName.value.toString()} from org.springframework.boot.autoconfigure.jdbc.DataSourceProperties x", o -> {
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
