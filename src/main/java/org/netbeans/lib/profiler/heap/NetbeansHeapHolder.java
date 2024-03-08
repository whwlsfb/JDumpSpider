package org.netbeans.lib.profiler.heap;


import cn.wanghw.IHeapHolder;
import cn.wanghw.utils._StringJoiner;
import org.netbeans.modules.profiler.oql.engine.api.impl.Snapshot;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetbeansHeapHolder implements IHeapHolder {
    final private AtomicBoolean cancelled = new AtomicBoolean(false);
    private Heap _heap;
    private Snapshot snapshot;

    public NetbeansHeapHolder(File heapfile) throws IOException {
        _heap = HeapFactory.createHeap(heapfile);
        snapshot = new Snapshot(_heap, this);
    }

    public Snapshot getSnapshot() {
        return new Snapshot(_heap, this);
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    public JavaClass findClass(String var1) {
        return snapshot.findClass(var1);
    }

    public Iterator getClasses() {
        return snapshot.getClasses();
    }

    public Object[] getSubClasses(Object javaClass) {
        if (javaClass instanceof JavaClass) {
            return ((JavaClass) javaClass).getSubClasses().toArray(new Object[0]);
        }
        return new JavaClass[0];
    }

    public List getInstances(Object javaClass) {
        if (javaClass instanceof JavaClass) {
            return ((JavaClass) javaClass).getInstances();
        }
        return new ArrayList();
    }

    public List getFields(Object javaClass) {
        if (javaClass instanceof JavaClass) {
            return ((JavaClass) javaClass).getFields();
        }
        return new ArrayList();
    }

    public List hasField(Object javaClass, String fieldName) {
        if (javaClass instanceof JavaClass) {
            return ((JavaClass) javaClass).getFields();
        }
        return new ArrayList();
    }

    public String getClassName(Object javaClass) {
        if (javaClass instanceof JavaClass) {
            return ((JavaClass) javaClass).getName();
        }
        return null;
    }

    public boolean isInstanceOf(Object javaClass, String className) {
        if (javaClass instanceof JavaClass) {
            JavaClass cls = (JavaClass) javaClass;
            for (; cls != null; cls = cls.getSuperClass())
                if (cls.getName().equals(className)) return true;
        }
        return false;
    }

    public boolean isArray(Object javaClass) {
        if (javaClass instanceof JavaClass) {
            return ((JavaClass) javaClass).isArray();
        }
        return false;
    }

    public Object getSuperClass(Object javaClass) {
        if (javaClass instanceof JavaClass) {
            return ((JavaClass) javaClass).getSuperClass();
        }
        return null;
    }

    public String getFieldName(Object field) {
        if (field instanceof Field) {
            return ((Field) field).getName();
        }
        return null;
    }

    public Object getFieldClass(Object field) {
        if (field instanceof Field) {
            return snapshot.findClass(((Field) field).getType().getName());
        }
        return null;
    }

    public Object findThing(Long objectId) {
        return snapshot.findThing(objectId);
    }

    public Object getValueOfField(Object instance, String fieldName) {
        if (instance instanceof Instance) {
            return ((Instance) instance).getValueOfField(fieldName);
        }
        return null;
    }

    public void cancelQuery() {
        cancelled.set(true);
    }

    public HashMap<String, String> getFieldsByNameList(Object instance, HashMap<String, String> fieldList) {
        HashMap<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, String> fieldName : fieldList.entrySet()) {
            result.put(fieldName.getKey(), getFieldStringValue(instance, fieldName.getValue()));
        }
        return result;
    }

    public HashMap<String, String> arrayDump(Object instance) {
        HashMap<String, String> result = new HashMap<String, String>();
        if (instance instanceof ObjectArrayDump) {
            ObjectArrayDump arrayDump = (ObjectArrayDump) instance;
            for (Object _entry : arrayDump.getValues()) {
                if (_entry == null) continue;
                String val = getFieldStringValue((Instance) _entry, "value");
                if (val != null && !val.equals("")) {
                    result.put(getFieldStringValue((Instance) _entry, "key"), val);
                }
            }
        }
        return result;
    }

    public Instance[] getArrayItems(Object instance) {
        if (instance instanceof ObjectArrayDump) {
            ObjectArrayDump arrayDump = (ObjectArrayDump) instance;
            return (Instance[]) arrayDump.getValues().toArray(new Instance[0]);
        }
        return new Instance[0];
    }

    public String getFieldStringValue(Object instance, String fieldName) {
        Object val = getFieldValue(instance, fieldName);
        if (val instanceof Instance) {
            return toString((Instance) val);
        } else if (val != null) {
            return String.valueOf(val);
        }
        return null;
    }

    public Object getFieldValue(Object _instance, String fieldName) {
        Instance instance = (Instance) _instance;
        if (fieldName.contains(".")) {
            Object fInstance = instance.getValueOfField(fieldName.substring(0, fieldName.indexOf(".")));
            if (fInstance != null) {
                return getFieldValue(fInstance, fieldName.substring(fieldName.indexOf(".") + 1));
            } else {
                return null;
            }
        } else {
            if (fieldName.equals("@ID"))
                return String.valueOf(instance.getInstanceId());
            Object fInstance = instance.getValueOfField(fieldName);
            if (fInstance != null) {
                if (fInstance instanceof Integer) {
                    return String.valueOf(fInstance);
                }
                return instance.getValueOfField(fieldName);
            } else {
                return null;
            }
        }
    }

    public String toString(Object _instance) {
        Instance instance = (Instance) _instance;
        String instanceClassName = instance.getJavaClass().getName();
        if (instanceClassName.equals("java.lang.String")) {
            PrimitiveArrayDump arrayDump = (PrimitiveArrayDump) (instance.getValueOfField("value"));
            if (arrayDump == null)
                return "";
            if (arrayDump.getJavaClass().getName().equals("byte[]")) {
                List<String> byteStr = arrayDump.getValues();
                byte[] target = new byte[byteStr.size()];
                for (int i = 0; i < byteStr.size(); i++) {
                    target[i] = (byte) Integer.parseInt(byteStr.get(i));
                }
                return new String(target);
            }
            return join("", arrayDump.getValues());
        } else if (instanceClassName.equals("char[]")) {
            return join("", ((PrimitiveArrayDump) instance).getValues());
        } else {
            Object val = instance.getValueOfField("value");
            if (val instanceof Instance) {
                return toString(val);
            }
        }
        return null;
    }

    public byte[] toByteArray(Object _instance) {
        if (_instance instanceof PrimitiveArrayDump) {
            PrimitiveArrayDump arrayDump = (PrimitiveArrayDump) _instance;
            if (arrayDump.getJavaClass().getName().equals("byte[]")) {
                List<String> byteStr = arrayDump.getValues();
                byte[] target = new byte[byteStr.size()];
                for (int i = 0; i < byteStr.size(); i++) {
                    target[i] = (byte) Integer.parseInt(byteStr.get(i));
                }
                return target;
            }
        }
        return null;
    }

    public String join(CharSequence delimiter,
                       Iterable<? extends CharSequence> elements) {
        _StringJoiner.requireNonNull(delimiter);
        _StringJoiner.requireNonNull(elements);
        _StringJoiner joiner = new _StringJoiner(delimiter);
        for (CharSequence cs : elements) {
            joiner.add(cs);
        }
        return joiner.toString();
    }

    static final List<String> mapClassList = Arrays.asList(
            "java.util.HashMap",
            "java.util.Properties",
            "java.util.LinkedHashMap",
            "java.util.Collections$UnmodifiableMap"
    );

    public boolean isMap(Object _instance) {
        if (_instance != null) {
            Instance instance = (Instance) _instance;
            String className = instance.getJavaClass().getName();
            return mapClassList.contains(className);
        } else return false;
    }

    public Instance getMap(Object _instance) {
        if (_instance != null) {
            Instance instance = (Instance) _instance;
            Object table = instance.getValueOfField("table");
            if (table == null)
                table = getFieldValue(instance, "source.table");
            if (table != null) {
                return (Instance) table;
            } else {
                Object m1 = instance.getValueOfField("m");
                if (m1 != null) {
                    Object m2 = ((Instance) m1).getValueOfField("m");
                    if (m2 != null) {
                        return (Instance) ((Instance) m2).getValueOfField("table");
                    } else {
                        return (Instance) ((Instance) m1).getValueOfField("table");
                    }
                } else {
                    return null;
                }
            }
        } else return null;
    }
}
