package org.graalvm.visualvm.lib.profiler.oql.engine.api.impl;

public interface ReachableExcludes {
    /**
     * @return true if the given field is on the hitlist of excluded
     * 		fields.
     */
    public boolean isExcluded(String fieldName);
}
