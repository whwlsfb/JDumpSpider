package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.HashMap;


public class DataSource04 implements ISpider {

    public String getName() {
        return "AliDruidDataSourceWrapper";
    }


    public String sniff(IHeapHolder heapHolder) {
        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceWrapper");
            if (clazz == null)
                return null;
            HashMap<String, String> fieldList = new HashMap<String, String>() {{
                put("username", "username");
                put("password", "password");
                put("jdbcUrl", "jdbcUrl");
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
