package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;

import java.util.HashMap;

public class DataSource03 implements ISpider {

    public String getName() {
        return "MongoClient";
    }


    public String sniff(IHeapHolder heapHolder) {
        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("com.mongodb.MongoClient");
            if (clazz == null)
                return null;
            HashMap<String, String> fieldList = new HashMap<String, String>() {{
                put("host", "cluster.settings.hosts.list.elementData.host");
                put("port", "cluster.settings.hosts.list.elementData.port");
                put("username", "credentialsList.list.elementData.userName");
                put("password", "credentialsList.list.elementData.password");
                put("database", "credentialsList.list.elementData.source");
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
