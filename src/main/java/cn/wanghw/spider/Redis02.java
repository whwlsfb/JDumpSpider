package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.HashMap;

public class Redis02 implements ISpider {

    public String getName() {
        return "JedisClient";
    }


    public String sniff(IHeapHolder heapHolder) {
        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("redis.clients.jedis.Client");
            if (clazz == null)
                return null;
            HashMap<String, String> fieldList = new HashMap<String, String>() {{
                put("hostname", "hostname");
                put("port", "port");
                put("password", "password");
                put("database", "db");
            }};
            for (Object instance : heapHolder.getInstances(clazz)) {
                result.append(HashMapUtils.dumpString(heapHolder.getFieldsByNameList(instance, fieldList)));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result.toString();
    }
}