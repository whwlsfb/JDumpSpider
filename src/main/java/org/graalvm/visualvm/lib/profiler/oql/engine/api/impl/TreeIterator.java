package org.graalvm.visualvm.lib.profiler.oql.engine.api.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

/**
 * Provides an iterator over instances of <I> using instances of <T> for traversal
 * @author Jaroslav Bachorik
 */
abstract public class TreeIterator<I, T> implements Iterator<I> {
    private Stack<T> toInspect = new Stack<T>();
    private Set<T> inspected = new HashSet<T>();

    private T popped = null;
    private Iterator<I> inspecting = null;

    public TreeIterator(T root) {
        toInspect.push(root);
        inspected.add(root);
    }

    public boolean hasNext() {
        setupIterator();
        return inspecting != null && inspecting.hasNext();
    }

    public I next() {
        setupIterator();

        if (inspecting == null || !inspecting.hasNext()) {
            throw new NoSuchElementException();
        }

        I retVal = inspecting.next();
        return retVal;
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); // NOI18N
    }

    abstract protected Iterator<I> getSameLevelIterator(T popped);
    abstract protected Iterator<T> getTraversingIterator(T popped);

    private void setupIterator() {
        while (!toInspect.isEmpty() && (inspecting == null || !inspecting.hasNext())) {
            popped = toInspect.pop();
            if (popped != null) {
                inspecting = getSameLevelIterator(popped);
                Iterator<T> recurseIter = getTraversingIterator(popped);
                while (recurseIter.hasNext()) {
                    T inspectNext = recurseIter.next();
                    if (inspectNext == null) continue;
                    if (!inspected.contains(inspectNext)) {
                        toInspect.push(inspectNext);
                        inspected.add(inspectNext);
                    }
                }
            } else {
                inspecting = null;
            }
        }
    }
}
