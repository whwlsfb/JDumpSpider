package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.HashMap;

public class DataSource02 implements ISpider {
    
    public String getName() {
        return "WeblogicDataSourceConnectionPoolConfig";
    }

    
    public String sniff(IHeapHolder heapHolder) {
        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("weblogic.jdbc.common.internal.DataSourceConnectionPoolConfig");
            if (clazz == null)
                return null;
            HashMap<String, String> fieldList = new HashMap<String, String>() {{
                put("url", "url");
                put("driver", "driver");
                put("name", "name");
                put("username", "defaultConnectionInfo.username");
                put("password", "defaultConnectionInfo.p");
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
