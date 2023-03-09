package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class OSS01 implements ISpider {
    public String getName() {
        return "OSS";
    }

    final List<String> ossKeywords = Collections.unmodifiableList(Arrays.asList("key", "id", "secret", "access", "bucket", "endpoint"));

    private boolean judge(String key) {
        key = key.toLowerCase();
        if (key.contains("oss.") || key.contains("cos.") || (key.contains("file") && key.contains("upload"))) {
            for (String keyword : ossKeywords) {
                if (key.contains(keyword)) return true;
            }
        }
        return false;
    }

    public String sniff(IHeapHolder heapHolder) {
        final StringBuilder result = new StringBuilder();
        try {
            LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
            Object clazz = heapHolder.findClass("java.util.Hashtable$Entry");
            if (clazz != null)
                for (Object instance : heapHolder.getInstances(clazz)) {
                    String key = heapHolder.getFieldStringValue(instance, "key");
                    if (key != null && judge(key)) {
                        String val = heapHolder.getFieldStringValue(instance, "value");
                        if (val != null && !val.equals("")) {
                            values.put(key, val);
                        }
                    }
                }
            clazz = heapHolder.findClass("java.util.LinkedHashMap$Entry");
            if (clazz != null)
                for (Object instance : heapHolder.getInstances(clazz)) {
                    String key = heapHolder.getFieldStringValue(instance, "key");
                    if (key != null && judge(key)) {
                        String val = heapHolder.getFieldStringValue(instance, "value");
                        if (val != null && !val.equals("")) {
                            values.put(key, val);
                        }
                    }
                }
            result.append(HashMapUtils.dumpString(values, false));
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result.toString();
    }
}