package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.HashMap;

public class PropertySource01 implements ISpider {

    public String getName() {
        return "OriginTrackedMapPropertySource";
    }


    public String sniff(IHeapHolder heapHolder) {

        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("org.springframework.boot.env.OriginTrackedMapPropertySource");
            if (clazz == null)
                return null;
            HashMap<String, String> values = new HashMap<String, String>();
            for (Object instance : heapHolder.getInstances(clazz)) {
                Object source = heapHolder.getFieldValue(instance, "source");
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
