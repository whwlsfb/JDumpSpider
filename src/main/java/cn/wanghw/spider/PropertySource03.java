package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.CommonUtils;
import cn.wanghw.utils.OQLSnippets;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertySource03 implements ISpider {

    @Override
    public String getName() {
        return "MapPropertySources";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        long currentObjId = 0;
        try {
            List<Long> listObjId = new ArrayList<>();
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery(OQLSnippets.getTable + OQLSnippets.isMap + "map(filter(map(filter(map(heap.objects('org.springframework.core.env.MapPropertySource'), 'it.source'), 'isMap(it)'), 'getTable(it)'), 'it != null'), 'it.id');", o -> {
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
                System.out.println(ex + " objId: " + currentObjId);
            }
        }
        return result[0];
    }
}
