package cn.wanghw;

import cn.wanghw.spider.*;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.jfluid.heap.HeapFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "JDumpSpider", mixinStandardHelpOptions = true,
        description = "Extract sensitive information from heapdump file.")
public class Main implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "Heap file path.")
    private File heapfile;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    private ISpider[] allSpiders = new ISpider[]{
            new DataSource01(),
            new DataSource02(),
            new DataSource03(),
            new DataSource04(),
            new Redis01(),
            new Redis02(),
            new ShiroKey01(),
            new PropertySource01(),
            new PropertySource02(),
            new PropertySource03(),
            new OSS01()
    };

    @Override
    public Integer call() throws Exception {
        Heap heap = HeapFactory.createHeap(heapfile);
        for (ISpider spider : allSpiders) {
            System.out.println("===========================================");
            System.out.println(spider.getName());
            System.out.println("-------------");
            String result = spider.sniff(heap);
            if (!result.equals("")) {
                System.out.println(result);
            } else {
                System.out.println("not found!\r\n");
            }
        }
        System.out.println("===========================================");
        return 0;
    }
}
