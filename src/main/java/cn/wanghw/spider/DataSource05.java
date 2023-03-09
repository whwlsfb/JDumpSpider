package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.HashMap;


public class DataSource05 implements ISpider {

    public String getName() {
        return "HikariDataSource";
    }


    public String sniff(IHeapHolder heapHolder) {

        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("com.zaxxer.hikari.util.DriverDataSource");
            if (clazz == null)
                return null;
            HashMap<String, String> fieldList = new HashMap<String, String>() {{
                put("jdbcUrl", "jdbcUrl");
                put("tableId", "driverProperties.table.@ID");
            }};
            for (Object instance : heapHolder.getInstances(clazz)) {
                HashMap<String, String> fieldValue = heapHolder.getFieldsByNameList(instance, fieldList);
                Object paramsTable = heapHolder.findThing(Long.parseLong(fieldValue.get("tableId")));
                fieldValue.remove("tableId");
                fieldValue.putAll(heapHolder.arrayDump(paramsTable));
                result.append(HashMapUtils.dumpString(fieldValue, false));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result.toString();
    }
}
