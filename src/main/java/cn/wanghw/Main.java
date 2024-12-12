package cn.wanghw;

import cn.wanghw.spider.*;
import org.graalvm.visualvm.lib.jfluid.heap.GraalvmHeapHolder;
import org.netbeans.lib.profiler.heap.NetbeansHeapHolder;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {

    private File heapfile;
    private final List<String> flag = new LinkedList<String>();
    static PrintStream out = null;

    public static String run(String[] args) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if (out == null) {
            out = new PrintStream(bout);
        }
        if (args.length < 1) {
            System.out.println("please give a heap filepath.");
        } else {
            Main _main = new Main();
            _main.heapfile = new File(args[0]);
            if (_main.heapfile.exists() && _main.heapfile.isFile()) {
                if (args.length > 1) {
                    _main.flag.addAll(Arrays.asList(args).subList(1, args.length));
                }
                _main.call(out);
            } else {
                System.out.println("file not exist!");
            }
        }
        return bout.toString();
    }

    public static String runAsync(final String[] args) throws Exception {
        if (args.length < 2)
            return "In async call, you must give a result file path";
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    String result = Main.run(args);
                    FileOutputStream fos = new FileOutputStream(args[1]);
                    fos.write(result.getBytes());
                    fos.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return "start export thread:" + thread.getName();
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        run(args);
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
            new UserPassSearcher01(),
            new CookieThief(),
            new AuthThief()
    };

    public int call(PrintStream out) throws Exception {
        int ver = getFileVersion();
        float classVersion = Float.parseFloat(System.getProperty("java.class.version"));
        IHeapHolder heapHolder;

        if (ver == 1 || classVersion < 52) {
            heapHolder = new NetbeansHeapHolder(heapfile);
        } else {
            heapHolder = new GraalvmHeapHolder(heapfile);
        }
        if (flag.contains("export-strings")) {
            spiderCall(new ExportAllString(), heapHolder, out);
            return 0;
        }
        if (flag.contains("-out")) {
            String outFilePath = getArgValue("-out");
            System.out.println("[+] Output to: " + outFilePath);
            out = new PrintStream(new FileOutputStream(outFilePath), true);
        }
        for (ISpider spider : allSpiders) {
            spiderCall(spider, heapHolder, out);
        }
        out.println("===========================================");
        return 0;
    }

    private String getArgValue(String flagStr) throws Exception {
        try {
            return flag.get(flag.indexOf(flagStr) + 1);
        } catch (IndexOutOfBoundsException e) {
            throw new Exception("[-] Get '" + flagStr + "' value failed!");
        }
    }

    private void spiderCall(ISpider spider, IHeapHolder heapHolder, PrintStream out) {
        out.println("===========================================");
        out.println(spider.getName());
        out.println("-------------");
        String result = spider.sniff(heapHolder);
        if (!(result == null) && !result.equals("")) {
            out.println(result);
        } else {
            out.println("not found!\r\n");
        }
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
