package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.HashMap;


public class Redis01 implements ISpider {

    public String getName() {
        return "RedisStandaloneConfiguration";
    }


    public String sniff(IHeapHolder heapHolder) {
        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("org.springframework.data.redis.connection.RedisStandaloneConfiguration");
            if (clazz == null)
                return null;
            HashMap<String, String> fieldList = new HashMap<String, String>() {{
                put("hostName", "hostName");
                put("port", "port");
                put("password", "password.thePassword");
                put("database", "database");
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