package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataSource03 implements ISpider {
    @Override
    public String getName() {
        return "MongoClient";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            List<Long> hostsObjId = new ArrayList<>();
            List<Long> accountObjId = new ArrayList<>();
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select { 'hosts': x.cluster.settings.hosts.list.elementData.id.toString(),'accounts':x.credentialsList.list.elementData.id.toString()} from com.mongodb.MongoClient x", o -> {
                if (o instanceof HashMap) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) o;
                    hostsObjId.add(Long.parseLong(hashMap.get("hosts")));
                    accountObjId.add(Long.parseLong(hashMap.get("accounts")));
                }
                return false;
            });
            if (hostsObjId.size() > 0) {
                result[0] += "Found Mongo hosts: \r\n";
                for (Long objId : hostsObjId) {
                    oqlEngine.executeQuery("select map(heap.findObject(" + objId.toString() + "), \"{'host': it.host.toString(), 'port':it.port.toString()}\")", o -> {
                        if (o instanceof HashMap) {
                            HashMap<String, String> hashMap = (HashMap<String, String>) o;
                            result[0] += "- " + HashMapUtils.dumpString(hashMap);
                        }
                        return false;
                    });
                }
            }
            if (accountObjId.size() > 0) {
                result[0] += "Found Mongo credentials: \r\n";
                for (Long objId : accountObjId) {
                    oqlEngine.executeQuery("select map(filter(map(heap.findObject(" + objId.toString() + "), \"it\"),'it!=null'),\"{'username':it.userName.toString(), 'password':it.password.toString(), 'database':it.source.toString()}\")", o -> {
                        if (o instanceof HashMap) {
                            HashMap<String, String> hashMap = (HashMap<String, String>) o;
                            result[0] += "- " + HashMapUtils.dumpString(hashMap);
                        }
                        return false;
                    });
                }
            }
        } catch (Exception ex) {
            if (!result[0].equals("")) {
                result[0] = "not found!";
            }
        }
        return result[0];
    }
}
