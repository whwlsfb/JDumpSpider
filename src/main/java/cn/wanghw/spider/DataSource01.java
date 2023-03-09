package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.HashMap;


public class DataSource01 implements ISpider {

    public String getName() {
        return "SpringDataSourceProperties";
    }

    public String sniff(IHeapHolder heapHolder) {
        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("org.springframework.boot.autoconfigure.jdbc.DataSourceProperties");
            if (clazz == null)
                return null;
            HashMap<String, String> fieldList = new HashMap<String, String>() {{
                put("driverClassName", "driverClassName");
                put("username", "username");
                put("password", "password");
                put("url", "url");
            }};
            for (Object instance : heapHolder.getInstances(clazz)) {
                result.append(HashMapUtils.dumpString(heapHolder.getFieldsByNameList(instance, fieldList), false));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result.toString();
    }
}
