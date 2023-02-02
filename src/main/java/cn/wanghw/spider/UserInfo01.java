package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;
import cn.wanghw.utils.OQLSnippets;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserInfo01 implements ISpider {
    @Override
    public String getName() {
        return "SpringSecurityUsernamePasswordAuthenticationToken(UserInfo)";
    }

    @Override
    public String sniff(Heap heap) {
        final StringBuilder result = new StringBuilder();
        try {
            AtomicInteger index = new AtomicInteger();
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery(OQLSnippets.unwrapMapEx + "map(heap.objects('org.springframework.security.authentication.UsernamePasswordAuthenticationToken'), 'unwrapMapEx(it.principal)');", o -> {
                if (o instanceof HashMap) {
                    result.append("# No.").append(index.incrementAndGet()).append(":\r\n");
                    result.append(HashMapUtils.dumpString((HashMap<String, String>) o, false));
                }
                return false;
            });
        } catch (Exception ex) {
            if (result.toString().equals("") && ex.getMessage().contains("is not found!")) {
                result.append("not found!\r\n");
            } else {
                System.out.println(ex);
            }
        }
        return result.toString();
    }

}
