package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.HashMap;

public class OSS01 implements ISpider {
    @Override
    public String getName() {
        return "OSS";
    }

    private static final String searchPart =
             "( (x.key.toString().toLowerCase().contains('oss.') || " +
                "x.key.toString().toLowerCase().contains('fileupload') || " +
                "x.key.toString().toLowerCase().contains('file.upload')) && " +
                    "(x.key.toString().toLowerCase().contains('keyid') || x.key.toString().toLowerCase().contains('key.id') || " +
                    "x.key.toString().toLowerCase().contains('keysecret') || x.key.toString().toLowerCase().contains('key.secret') ||" +
                    "x.key.toString().toLowerCase().contains('bucketname') || " +
                    "x.key.toString().toLowerCase().contains('endpoint')))";

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select {'key': x.key.toString(), 'value': x.value.toString()} from java.util.Hashtable$Entry x where x.key != null && " + searchPart, o -> {
                if (o instanceof HashMap) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) o;
                    result[0] += hashMap.get("key") + " = " + hashMap.get("value") + "\r\n";
                }
                return false;
            });
            oqlEngine.executeQuery("select {'key': x.key.toString(), 'value': x.value.toString()} from java.util.LinkedHashMap$Entry x where x.key != null && " + searchPart, o -> {
                if (o instanceof HashMap) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) o;
                    result[0] += hashMap.get("key") + " = " + hashMap.get("value") + "\r\n";
                }
                return false;
            });
        } catch (Exception ex) {
            if (result[0].equals("") && ex.getMessage().contains("is not found!")) {
                result[0] = "not found!\r\n";
            } else {
                System.out.println(ex);
            }
        }
        return result[0];
    }
}