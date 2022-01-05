package cn.wanghw;

import org.graalvm.visualvm.lib.jfluid.heap.Heap;

public interface ISpider {
    String getName();
    String sniff(Heap heap);
}
