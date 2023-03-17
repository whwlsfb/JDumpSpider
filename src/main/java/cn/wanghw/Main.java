package cn.wanghw;

import cn.wanghw.spider.*;
import org.graalvm.visualvm.lib.jfluid.heap.GraalvmHeapHolder;
import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.HeapFactory;
import org.netbeans.lib.profiler.heap.NetbeansHeapHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.sql.JDBCType;

public class Main {

    private File heapfile;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("please give a heap filepath.");
            System.exit(-1);
        } else {
            Main _main = new Main();
            _main.heapfile = new File(args[0]);
            if (_main.heapfile.exists()) {
                _main.call();
            } else {
                System.out.println("file not exist!");
                System.exit(-1);
            }
        }
    }

    private ISpider[] allSpiders = new ISpider[]{
            new DataSource01(),
            new DataSource02(),
            new DataSource03(),
            new DataSource04(),
            new DataSource05(),
            new Redis01(),
            new Redis02(),
            new ShiroKey01(),
            new PropertySource01(),
            new PropertySource02(),
            new PropertySource03(),
            new PropertySource04(),
////            new JwtKey01(),
            new PropertySource05(),
            new EnvProperty01(),
            new OSS01(),
            new UserPassSearcher01()
    };

    public Integer call() throws Exception {
        int ver = getFileVersion();
        float classVersion = Float.parseFloat(System.getProperty("java.class.version"));
        IHeapHolder heapHolder;
        if (ver == 1 || classVersion < 52) {
            heapHolder = new NetbeansHeapHolder(heapfile);
        } else {
            heapHolder = new GraalvmHeapHolder(heapfile);
        }
        for (ISpider spider : allSpiders) {
            System.out.println("===========================================");
            System.out.println(spider.getName());
            System.out.println("-------------");
            String result = spider.sniff(heapHolder);
            if (!(result == null) && !result.equals("")) {
                System.out.println(result);
            } else {
                System.out.println("not found!\r\n");
            }
        }

        System.out.println("===========================================");

        return 0;
    }

    public int getFileVersion() {
        try {
            FileInputStream io = new FileInputStream(heapfile);
            io.skip(17);
            byte subVersion = (byte) io.read();
            return Integer.parseInt(Character.valueOf((char) subVersion).toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
