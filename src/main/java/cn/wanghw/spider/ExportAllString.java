package cn.wanghw.spider;

import cn.wanghw.IHeapHolder;
import cn.wanghw.ISpider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ExportAllString implements ISpider {

    public String getName() {
        return "ExportAllString";
    }

    public String sniff(IHeapHolder heapHolder) {
        try {
            File outFile = new File(System.nanoTime() + ".txt");
            PrintWriter pw = new PrintWriter(new FileOutputStream(outFile));
            System.out.println("[+] Output to: " + outFile.getAbsolutePath());
            Object clazz = heapHolder.findClass("java.lang.String");
            if (clazz == null)
                return null;
            for (Object instance : heapHolder.getInstances(clazz)) {
                pw.println(heapHolder.toString(instance));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return "\r\n";
    }
}
