package cn.wanghw;


import org.graalvm.visualvm.lib.jfluid.heap.Instance;
import org.graalvm.visualvm.lib.jfluid.heap.JavaClass;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public interface IHeapHolder {
    Object findClass(String var1);

    Iterator getClasses();

    boolean isInstanceOf(Object javaClass, String className);

    boolean isArray(Object javaClass);

    Object[] getSubClasses(Object javaClass);

    List getInstances(Object javaClass);

    List getFields(Object javaClass);

    String getClassName(Object javaClass);

    Object getSuperClass(Object javaClass);

    String getFieldName(Object field);

    Object getFieldClass(Object field);

    Object findThing(Long objectId);

    Object getValueOfField(Object instance, String fieldName);

    HashMap<String, String> getFieldsByNameList(Object instance, HashMap<String, String> fieldList);

    HashMap<String, String> arrayDump(Object instance);

    Object[] getArrayItems(Object instance);

    String getFieldStringValue(Object instance, String fieldName);

    Object getFieldValue(Object instance, String fieldName);

    boolean isMap(Object instance);

    Object getMap(Object instance);

    String toString(Object instance);

    byte[] toByteArray(Object _instance);
}
