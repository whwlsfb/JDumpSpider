//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.netbeans.modules.profiler.oql.engine.api.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

public abstract class TreeIterator<I, T> implements Iterator<I> {
    private Stack<T> toInspect = new Stack();
    private Set<T> inspected = new HashSet();
    private T popped = null;
    private Iterator<I> inspecting = null;

    public TreeIterator(T var1) {
        this.toInspect.push(var1);
        this.inspected.add(var1);
    }

    public boolean hasNext() {
        this.setupIterator();
        return this.inspecting != null && this.inspecting.hasNext();
    }

    public I next() {
        this.setupIterator();
        if (this.inspecting != null && this.inspecting.hasNext()) {
            Object var1 = this.inspecting.next();
            return (I) var1;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected abstract Iterator<I> getSameLevelIterator(T var1);

    protected abstract Iterator<T> getTraversingIterator(T var1);

    private void setupIterator() {
        while(!this.toInspect.isEmpty() && (this.inspecting == null || !this.inspecting.hasNext())) {
            this.popped = this.toInspect.pop();
            if (this.popped != null) {
                this.inspecting = this.getSameLevelIterator(this.popped);
                Iterator var1 = this.getTraversingIterator(this.popped);

                while(var1.hasNext()) {
                    Object var2 = var1.next();
                    if (var2 != null && !this.inspected.contains(var2)) {
                        this.toInspect.push((T) var2);
                        this.inspected.add((T) var2);
                    }
                }
            } else {
                this.inspecting = null;
            }
        }

    }
}
