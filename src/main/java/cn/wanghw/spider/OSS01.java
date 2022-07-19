package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;
import cn.wanghw.utils.OQLSnippets;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.HashMap;

public class OSS01 implements ISpider {
    @Override
    public String getName() {
        return "OSS";
    }

    private static final String searchPart =
             "((it.searchKey.contains('oss.') || " + //aliyun oss
              "it.searchKey.contains('cos.') || " + //tencent cos
                     "(it.searchKey.contains('file') && " +
                     "it.searchKey.contains('upload')) ) && " +
             "((it.searchKey.contains('key') && " +
                     "(it.searchKey.contains('id') || " +
                     "it.searchKey.contains('secret') || " +
                     "it.searchKey.contains('access'))) || " +
             "it.searchKey.contains('bucket') || " +
             "it.searchKey.contains('endpoint')))";

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery(OQLSnippets.getValue + "filter(map(filter(heap.objects('java.util.Hashtable$Entry'),'it.key != null'), \"{'key': it.key.toString(),'searchKey': it.key.toString().toLowerCase(), 'value': getValue(it.value)}\"), \"" + searchPart + "\")", o -> {
                if (o instanceof HashMap) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) o;
                    result[0] += hashMap.get("key") + " = " + hashMap.get("value") + "\r\n";
                }
                return false;
            });
            oqlEngine.executeQuery(OQLSnippets.getValue + "filter(map(filter(heap.objects('java.util.LinkedHashMap$Entry'),'it.key != null'), \"{'key': it.key.toString(),'searchKey': it.key.toString().toLowerCase(), 'value': getValue(it.value)}\"), \"" + searchPart + "\")", o -> {
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