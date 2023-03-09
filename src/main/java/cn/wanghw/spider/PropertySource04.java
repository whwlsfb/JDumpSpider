package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.HashMap;

public class PropertySource04 implements ISpider {


    public String getName() {
        return "ConsulPropertySources";
    }


    public String sniff(IHeapHolder heapHolder) {
        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("org.springframework.cloud.consul.config.ConsulPropertySource");
            if (clazz == null)
                return null;
            HashMap<String, String> values = new HashMap<String, String>();
            for (Object instance : heapHolder.getInstances(clazz)) {
                Object source = heapHolder.getFieldValue(instance, "properties");
                if (heapHolder.isMap(source)) {
                    values.putAll(heapHolder.arrayDump(heapHolder.getMap(source)));
                }
            }
            result.append(HashMapUtils.dumpString(values, false));
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result.toString();
    }
}
