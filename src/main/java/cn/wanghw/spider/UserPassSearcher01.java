package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.*;

public class UserPassSearcher01 implements ISpider {
    public String getName() {
        return "UserPassSearcher";
    }

    static final List<String> keywordList = Collections.unmodifiableList(Arrays.asList(
            "username",
            "pass"
    ));

    static final List<String> unimportantKeywordList = Collections.unmodifiableList(Arrays.asList(
            "host",
            "user",
            "access",
            "key",
            "jdbc",
            "url",
            "token",
            "database",
            "db",
            "secret",
            "phone",
            "email",
            "enterprise",
            "login",
            "server",
            "addr"
    ));

    public String sniff(IHeapHolder heapHolder) {
        final StringBuilder result = new StringBuilder();
        try {
            for (Iterator it = heapHolder.getClasses(); it.hasNext(); ) {
                Object clazz = it.next();
                List<String> fieldList = new LinkedList<String>();
                fieldList.addAll(getFields(heapHolder, clazz, keywordList));
                if (fieldList.isEmpty()) continue;
                fieldList.addAll(getFields(heapHolder, clazz, unimportantKeywordList));
                List instances = heapHolder.getInstances(clazz);
                if (instances.isEmpty()) continue;
                HashMap<String, String> fieldMap = new HashMap<String, String>();
                for (String fieldName : fieldList) {
                    fieldMap.put(fieldName, fieldName);
                }
                StringBuilder subResult = new StringBuilder();
                boolean isAllEmpty = true;
                subResult.append(heapHolder.getClassName(clazz)).append(":\r\n");
                for (Object instance : instances) {
                    String dumpString = HashMapUtils.dumpString(heapHolder.getFieldsByNameList(instance, fieldMap), true, false, true);
                    if (!dumpString.equals("")) {
                        isAllEmpty = false;
                        subResult.append("[").append(dumpString).append("]\r\n");
                    }
                }
                if (!isAllEmpty) {
                    subResult.append("\r\n");
                    result.append(subResult);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result.toString();
    }

    public List<String> getFields(IHeapHolder heapHolder, Object clazz, List<String> keywordList) {
        List<String> fieldList = new LinkedList<String>();
        while (!heapHolder.getClassName(clazz).equals(Object.class.getName())) {
            for (Object f : heapHolder.getFields(clazz)) {
                String name = heapHolder.getFieldName(f).toLowerCase();
                for (String keyword : keywordList) {
                    if (name.contains(keyword)) {
                        fieldList.add(heapHolder.getFieldName(f));
                    }
                }
            }
            clazz = heapHolder.getSuperClass(clazz);
        }
        return fieldList;
    }
}
