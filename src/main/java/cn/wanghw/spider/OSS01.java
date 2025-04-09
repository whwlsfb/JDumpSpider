package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.*;

public class OSS01 implements ISpider {
    public String getName() {
        return "OSS";
    }

    final List<String> ossKeywords = Collections.unmodifiableList(Arrays.asList("key", "id", "secret", "access", "bucket", "endpoint", "sk"));

    private boolean judge(String key) {
        key = key.toLowerCase();
        if (key.contains("oss.") || key.contains("s3.") || key.contains("cos.") || key.contains("lbs.") || (key.contains("file") && key.contains("upload"))) {
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
            List<Object> mapEntryClasses = new ArrayList<Object>();
            for (Iterator it = heapHolder.getClasses(); it.hasNext(); ) {
                Object clazz = it.next();
                String clazzName = heapHolder.getClassName(clazz).toLowerCase();
                if (clazzName.contains("$") &&
                        (clazzName.split("\\$")[1].endsWith("entry") || clazzName.split("\\$")[1].endsWith("node")))
                    mapEntryClasses.add(clazz);
            }
            for (Object clazz : mapEntryClasses) {
                dump(heapHolder, values, clazz);
            }
            result.append(HashMapUtils.dumpString(values, false));
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result.toString();
    }

    private void dump(IHeapHolder heapHolder, LinkedHashMap<String, String> values, Object clazz) {
        for (Object instance : heapHolder.getInstances(clazz)) {
            String key = heapHolder.getFieldStringValue(instance, "key");
            if (key != null && judge(key)) {
                String val = heapHolder.getFieldStringValue(instance, "value");
                if (val != null && !val.equals("")) {
                    values.put(key, val);
                }
            }
        }
    }
}