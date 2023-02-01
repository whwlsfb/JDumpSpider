package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.CommonUtils;
import cn.wanghw.utils.HashMapUtils;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLException;

import java.util.ArrayList;
import java.util.HashMap;

public class DataSource05 implements ISpider {
    @Override
    public String getName() {
        return "HikariDataSource";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select {'jdbcUrl': x.jdbcUrl.toString(), 'tableId': x.driverProperties.table.id} from com.zaxxer.hikari.util.DriverDataSource x", o -> {
                if (o instanceof HashMap) {
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) o;
                    String jdbcUrl = (String) hashMap.get("jdbcUrl");
                    Long paramsTableId = ((Double) hashMap.get("tableId")).longValue();
                    result[0] += "jdbcUrl = " + jdbcUrl + "\r\n";
                    try {
                        result[0] += CommonUtils.dumpHashMaps(oqlEngine, new ArrayList<Long>() {{ add(paramsTableId);}});
                    } catch (OQLException e) {
                        e.printStackTrace();
                    }
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
