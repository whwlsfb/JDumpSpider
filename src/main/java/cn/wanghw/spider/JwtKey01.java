package cn.wanghw.spider;

import cn.wanghw.ISpider;
import cn.wanghw.utils.CommonUtils;
import cn.wanghw.utils.HashMapUtils;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.codec.binary.Base64;
import org.graalvm.visualvm.lib.jfluid.heap.Heap;
import org.graalvm.visualvm.lib.profiler.oql.engine.api.OQLEngine;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;

public class JwtKey01 implements ISpider {
    @Override
    public String getName() {
        return "SpringSecurityRsaSigner(JWT)";
    }

    @Override
    public String sniff(Heap heap) {
        final String[] result = {""};
        try {

            OQLEngine oqlEngine = new OQLEngine(heap);
            oqlEngine.executeQuery("select {'Algorithm':x.algorithm.toString(),'privateKey':{" +
                    "'coeff':x.key.coeff," +    // CRT coeffcient
                    "'qe':x.key.qe," +          // prime exponent q
                    "'pe':x.key.pe," +          // prime exponent p
                    "'q':x.key.q," +            // prime q
                    "'p':x.key.p," +            // prime p
                    "'d':x.key.d," +            // private exponent
                    "'e':x.key.e," +            // public exponent
                    "'n':x.key.n, " +           // modulus
                    "}} from org.springframework.security.jwt.crypto.sign.RsaSigner x", o -> {
                if (o instanceof HashMap) {
                    try {
                        HashMap<String, Object> hashMap = (HashMap<String, Object>) o;
                        HashMap<String, String> resultSet = new HashMap<>();
                        if (hashMap.containsKey("privateKey")) {
                            if (hashMap.get("privateKey") instanceof ScriptObjectMirror) {
                                ScriptObjectMirror privKey = (ScriptObjectMirror) hashMap.get("privateKey") ;
                                RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = new RSAPrivateCrtKeySpec(
                                        CommonUtils.getBigInteger(privKey.get("n")),
                                        CommonUtils.getBigInteger(privKey.get("e")),
                                        CommonUtils.getBigInteger(privKey.get("d")),
                                        CommonUtils.getBigInteger(privKey.get("p")),
                                        CommonUtils.getBigInteger(privKey.get("q")),
                                        CommonUtils.getBigInteger(privKey.get("pe")),
                                        CommonUtils.getBigInteger(privKey.get("qe")),
                                        CommonUtils.getBigInteger(privKey.get("coeff"))
                                );
                                RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(
                                        CommonUtils.getBigInteger(privKey.get("n")),
                                        CommonUtils.getBigInteger(privKey.get("e")));
                                KeyFactory keyFactory = null;
                                keyFactory = KeyFactory.getInstance("RSA");
                                resultSet.put("PrivateKey", Base64.encodeBase64String(keyFactory.generatePrivate(rsaPrivateCrtKeySpec).getEncoded()));
                                resultSet.put("PublicKey", Base64.encodeBase64String(keyFactory.generatePublic(pubSpec).getEncoded()));
                            }
                            if (hashMap.containsKey("Algorithm")) {
                                resultSet.put("Algorithm", hashMap.get("Algorithm").toString());
                            }
                        }
                        result[0] = HashMapUtils.dumpString(resultSet, false);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
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
