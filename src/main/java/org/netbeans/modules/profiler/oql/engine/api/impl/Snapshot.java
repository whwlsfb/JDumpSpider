//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.netbeans.modules.profiler.oql.engine.api.impl;

import org.netbeans.lib.profiler.heap.NetbeansHeapHolder;
import org.netbeans.lib.profiler.heap.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Snapshot {
    private final Heap delegate;
    private JavaClass weakReferenceClass;
    private int referentFieldIndex;
    private ReachableExcludes reachableExcludes;
    private final NetbeansHeapHolder engine;

    public Snapshot(Heap heap, NetbeansHeapHolder engine) {
        this.delegate = heap;
        this.engine = engine;
        this.init();
    }

    private void init() {
        this.weakReferenceClass = this.findClass("java.lang.ref.Reference");
        if (this.weakReferenceClass == null) {
            this.weakReferenceClass = this.findClass("sun.misc.Ref");
            this.referentFieldIndex = 0;
        } else {
            List var1 = this.weakReferenceClass.getFields();
            int var2 = var1.size();

            for(int var3 = 0; var3 < var2; ++var3) {
                if ("referent".equals(((Field)var1.get(var3)).getName())) {
                    this.referentFieldIndex = var3;
                    break;
                }
            }
        }

    }

    public JavaClass findClass(String var1) {
        try {
            long var2;
            if (var1.startsWith("0x")) {
                var2 = Long.parseLong(var1.substring(2), 16);
            } else {
                var2 = Long.parseLong(var1);
            }

            return this.delegate.getJavaClassByID(var2);
        } catch (NumberFormatException var4) {
            return this.delegate.getJavaClassByName(this.preprocessClassName(var1));
        }
    }

    private String preprocessClassName(String var1) {
        int var2 = 0;
        if (var1.startsWith("[")) {
            var2 = var1.lastIndexOf(91) + 1;
            var1 = var1.substring(var2);
        }

        if (var1.length() == 1) {
            if (var1.equals("I")) {
                var1 = "int";
            } else if (var1.equals("J")) {
                var1 = "long";
            } else if (var1.equals("D")) {
                var1 = "double";
            } else if (var1.equals("F")) {
                var1 = "float";
            } else if (var1.equals("B")) {
                var1 = "byte";
            } else if (var1.equals("S")) {
                var1 = "short";
            } else if (var1.equals("C")) {
                var1 = "char";
            } else if (var1.equals("Z")) {
                var1 = "boolean";
            }
        }

        if (var2 > 0 && var1.startsWith("L")) {
            var1 = var1.substring(1);
        }

        StringBuilder var3 = new StringBuilder(var1);

        for(int var4 = 0; var4 < var2; ++var4) {
            var3.append("[]");
        }

        return var3.toString();
    }

    public Instance findThing(long var1) {
        return this.delegate.getInstanceByID(var1);
    }

    public GCRoot findRoot(Instance var1) {
        Instance var2 = var1;

        do {
            var2 = var2.getNearestGCRootPointer();
        } while(!var2.isGCRoot());

        return var2 != null ? this.delegate.getGCRoot(var2) : null;
    }

    public Iterator getClasses() {
        return this.delegate.getAllClasses().iterator();
    }

    public Iterator getClassNames(String var1) {
        final Iterator var2 = this.delegate.getJavaClassesByRegExp(var1).iterator();
        return new Iterator() {
            public boolean hasNext() {
                return var2.hasNext();
            }

            public Object next() {
                return ((JavaClass)var2.next()).getName();
            }

            public void remove() {
                var2.remove();
            }
        };
    }

    public Iterator getInstances(JavaClass var1, final boolean var2) {
        return new TreeIterator<Instance, JavaClass>(var1) {
            protected Iterator<Instance> getSameLevelIterator(JavaClass var1) {
                return var1.getInstances().iterator();
            }

            protected Iterator<JavaClass> getTraversingIterator(JavaClass var1) {
                return var2 ? var1.getSubClasses().iterator() : Collections.EMPTY_LIST.iterator();
            }
        };
    }

    public Iterator getReferrers(Object var1, boolean var2) {
        ArrayList var3 = new ArrayList();
        ArrayList var4 = new ArrayList();
        if (var1 instanceof Instance) {
            var4.addAll(((Instance)var1).getReferences());
        } else if (var1 instanceof JavaClass) {
            var4.addAll(((JavaClass)var1).getInstances());
            var4.add(((JavaClass)var1).getClassLoader());
        }

        if (!var4.isEmpty()) {
            Iterator var5 = var4.iterator();

            while(true) {
                Instance var8;
                label41:
                do {
                    while(var5.hasNext()) {
                        Object var6 = var5.next();
                        if (var6 instanceof Value) {
                            Value var7 = (Value)var6;
                            var8 = var7.getDefiningInstance();
                            continue label41;
                        }

                        if (var6 instanceof Instance && (var2 || !this.isWeakRef((Instance)var6))) {
                            var3.add(var6);
                        }
                    }

                    return var3.iterator();
                } while(!var2 && this.isWeakRef(var8));

                var3.add(var8);
            }
        } else {
            return var3.iterator();
        }
    }

    public Iterator getReferees(Object var1, boolean var2) {
        ArrayList var3 = new ArrayList();
        ArrayList var4 = new ArrayList();
        if (var1 instanceof Instance) {
            Instance var5 = (Instance)var1;
            var4.addAll(var5.getFieldValues());
        }

        if (var1 instanceof JavaClass) {
            var4.addAll(((JavaClass)var1).getStaticFieldValues());
        }

        if (var1 instanceof ObjectArrayInstance) {
            ObjectArrayInstance var9 = (ObjectArrayInstance)var1;
            var4.addAll(var9.getValues());
        }

        if (!var4.isEmpty()) {
            Iterator var10 = var4.iterator();

            while(true) {
                Instance var7;
                label52:
                do {
                    while(var10.hasNext()) {
                        Object var6 = var10.next();
                        if (var6 instanceof ObjectFieldValue && ((ObjectFieldValue)var6).getInstance() != null) {
                            var7 = ((ObjectFieldValue)var6).getInstance();
                            continue label52;
                        }

                        if (var6 instanceof Instance && (var2 || !this.isWeakRef((Instance)var6))) {
                            var3.add(var6);
                        }
                    }

                    return var3.iterator();
                } while(!var2 && this.isWeakRef(var7));

                if (var7.getJavaClass().getName().equals("java.lang.Class")) {
                    JavaClass var8 = this.delegate.getJavaClassByID(var7.getInstanceId());
                    if (var8 != null) {
                        var3.add(var8);
                    } else {
                        var3.add(var7);
                    }
                } else {
                    var3.add(var7);
                }
            }
        } else {
            return var3.iterator();
        }
    }

    public Iterator getFinalizerObjects() {
        JavaClass var1 = this.findClass("java.lang.ref.Finalizer");
        Instance var2 = ((ObjectFieldValue)var1.getValueOfStaticField("queue")).getInstance();
        ObjectFieldValue var3 = (ObjectFieldValue)var2.getValueOfField("head");
        ArrayList var4 = new ArrayList();
        if (var3 != null) {
            Instance var5 = var3.getInstance();

            while(true) {
                ObjectFieldValue var6 = (ObjectFieldValue)var5.getValueOfField("referent");
                ObjectFieldValue var7 = (ObjectFieldValue)var5.getValueOfField("next");
                if (var7 == null || var7.getInstance().equals(var5)) {
                    break;
                }

                var5 = var7.getInstance();
                var4.add(var6.getInstance());
            }
        }

        return var4.iterator();
    }

    public Iterator getRoots() {
        return this.getRootsList().iterator();
    }

    public List getRootsList() {
        ArrayList var1 = new ArrayList();
        Iterator var2 = this.delegate.getGCRoots().iterator();

        while(var2.hasNext()) {
            Object var3 = var2.next();
            GCRoot var4 = (GCRoot)var3;
            Instance var5 = var4.getInstance();
            if (var5.getJavaClass().getName().equals("java.lang.Class")) {
                JavaClass var6 = this.delegate.getJavaClassByID(var5.getInstanceId());
                if (var6 != null) {
                    var1.add(var6);
                } else {
                    var1.add(var5);
                }
            } else {
                var1.add(var5);
            }
        }

        return var1;
    }

    public GCRoot[] getRootsArray() {
        List var1 = this.getRootsList();
        return (GCRoot[])((GCRoot[])var1.toArray(new GCRoot[var1.size()]));
    }

    private boolean isAssignable(JavaClass var1, JavaClass var2) {
        if (var1 == var2) {
            return true;
        } else {
            return var1 == null ? false : this.isAssignable(var1.getSuperClass(), var2);
        }
    }

    private boolean isWeakRef(Instance var1) {
        return this.weakReferenceClass != null && this.isAssignable(var1.getJavaClass(), this.weakReferenceClass);
    }

    public JavaClass getWeakReferenceClass() {
        return this.weakReferenceClass;
    }

    public int getReferentFieldIndex() {
        return this.referentFieldIndex;
    }

    public void setReachableExcludes(ReachableExcludes var1) {
        this.reachableExcludes = var1;
    }

    public ReachableExcludes getReachableExcludes() {
        return this.reachableExcludes;
    }

    public String valueString(Instance var1) {
        if (var1 == null) {
            return null;
        } else {
            try {
                if (var1.getJavaClass().getName().equals(String.class.getName())) {
                    Class var5 = Class.forName("org.netbeans.lib.profiler.heap.HprofProxy");
                    Method var6 = var5.getDeclaredMethod("getString", Instance.class);
                    var6.setAccessible(true);
                    return (String)var6.invoke(var5, var1);
                }

                if (var1.getJavaClass().getName().equals("char[]")) {
                    Method var2 = var1.getClass().getDeclaredMethod("getChars", Integer.TYPE, Integer.TYPE);
                    var2.setAccessible(true);
                    char[] var3 = (char[])((char[])var2.invoke(var1, 0, ((PrimitiveArrayInstance)var1).getLength()));
                    if (var3 != null) {
                        return new String(var3);
                    }

                    return "*null*";
                }
            } catch (Exception var4) {
                Logger.getLogger(Snapshot.class.getName()).log(Level.WARNING, "Error getting toString() value of an instance dump", var4);
            }

            return var1.toString();
        }
    }
}
