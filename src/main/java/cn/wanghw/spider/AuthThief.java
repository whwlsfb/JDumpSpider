package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.*;

public class AuthThief implements ISpider {
    public String getName() {
        return "AuthThief";
    }

    private boolean judge(String key) {
        key = key.toLowerCase();
        if (key.equals("authorization")) {
            return true;
        } else if (key.contains("auth")) {
            return true;
        } else if (key.contains("cookie")) {
            return true;
        }
        return false;
    }

    public String sniff(IHeapHolder heapHolder) {
        final StringBuilder result = new StringBuilder();
        try {
            List<Object> mapEntryClasses = new ArrayList<Object>();
            for (Iterator it = heapHolder.getClasses(); it.hasNext(); ) {
                Object clazz = it.next();
                String clazzName = heapHolder.getClassName(clazz).toLowerCase();
                if (clazzName.contains("$") && (clazzName.split("\\$")[1].endsWith("entry")))
                    mapEntryClasses.add(clazz);
            }
            for (Object clazz : mapEntryClasses) {
                LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
                dump(heapHolder, values, clazz);
                if (!values.isEmpty()) {
                    result.append(heapHolder.getClassName(clazz)).append(":\r\n");
                    result.append(HashMapUtils.dumpString(values, false)).append("\r\n");
                }
            }
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
        Object[] subClasses = heapHolder.getSubClasses(clazz);
        if (subClasses != null && subClasses.length > 0) {
            for (Object subClazz : subClasses) {
                dump(heapHolder, values, subClazz);
            }
        }
    }

    public List<Object> getFields(IHeapHolder heapHolder, Object clazz) {
        List<Object> fieldList = new LinkedList<Object>();
        while (!heapHolder.getClassName(clazz).equals(Object.class.getName())) {
            for (Object f : heapHolder.getFields(clazz)) {
                fieldList.add(f);
            }
            clazz = heapHolder.getSuperClass(clazz);
        }
        return fieldList;
    }
}
