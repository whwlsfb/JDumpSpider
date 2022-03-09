package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.HashMapUtils;
import org.apache.commons.codec.binary.Base64;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.jfluid.heap.PrimitiveArrayInstance;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ShiroKey01 implements ISpider {
    @Override
    public String getName() {
        return "CookieRememberMeManager(ShiroKey)";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {
            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select {'key':x.encryptionCipherKey,'algName':x.cipherService.algorithmName.toString(), 'algMode':x.cipherService.modeName.toString()} from org.apache.shiro.web.mgt.CookieRememberMeManager x", o -> {
                if (o instanceof HashMap) {
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) o;
                    HashMap<String, String> resultSet = new HashMap<>();
                    for (String key : hashMap.keySet()
                    ) {
                        Object obj = hashMap.get(key);
                        try {
                            if (obj instanceof String) {
                                resultSet.put(key, obj.toString());
                            } else if (obj.getClass() == Class.forName("org.graalvm.visualvm.lib.jfluid.heap.PrimitiveArrayDump")) {
                                Method getBytes = obj.getClass().getDeclaredMethod("getBytes", int.class, int.class);
                                Method getLength = obj.getClass().getMethod("getLength");
                                getBytes.setAccessible(true);
                                getLength.setAccessible(true);
                                byte[] keyBytes = (byte[]) getBytes.invoke(obj, 0, getLength.invoke(obj));
                                resultSet.put(key, Base64.encodeBase64String(keyBytes));
                            }
                        } catch (Exception ex) {
                        }
                    }
                    result[0] = HashMapUtils.dumpString(resultSet);
                }
                return false;
            });
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