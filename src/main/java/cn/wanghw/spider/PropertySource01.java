package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.CommonUtils;
import cn.wanghw.utils.HashMapUtils;
import cn.wanghw.utils.OQLSnippets;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertySource01 implements ISpider {

    @Override
    public String getName() {
        return "OriginTrackedMapPropertySource";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            List<Long> listObjId = new ArrayList<>();
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select map(filter(map(x.source.m ? x.source.m.table : x.source.table, 'it'), 'it != null'),\"it.id\") from org.springframework.boot.env.OriginTrackedMapPropertySource x", o -> {
                Long id = CommonUtils.getObjectId(o);
                if (id != null) {
                    listObjId.add(id);
                }
                return false;
            });
            result[0] = CommonUtils.dumpHashMaps(oqlEngine, listObjId);
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
