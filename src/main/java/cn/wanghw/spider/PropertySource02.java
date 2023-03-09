package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.HashMap;

public class PropertySource02 implements ISpider {


    public String getName() {
        return "MutablePropertySources";
    }


    public String sniff(IHeapHolder heapHolder) {

        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("org.springframework.core.env.MutablePropertySources");
            if (clazz == null)
                return null;
            HashMap<String, String> values = new HashMap<String, String>();
            for (Object instance : heapHolder.getInstances(clazz)) {
                Object[] array = heapHolder.getArrayItems(heapHolder.getFieldValue(instance, "propertySourceList.array"));
                for (Object source : array) {
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
