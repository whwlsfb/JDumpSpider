package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;

public class CookieThief implements ISpider {
    public String getName() {
        return "CookieThief";
    }

    public String sniff(IHeapHolder heapHolder)  {
        final StringBuilder result = new StringBuilder();
        try {
            Object clazz = heapHolder.findClass("java.lang.String");
            if (clazz == null)
                return null;
            for (Object instance : heapHolder.getInstances(clazz)) {
                String text = heapHolder.toString(instance);
                if (text.contains("Cookie:")) {
                    result.append(heapHolder.toString(instance)).append("\r\n");
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result.toString();
    }
}
