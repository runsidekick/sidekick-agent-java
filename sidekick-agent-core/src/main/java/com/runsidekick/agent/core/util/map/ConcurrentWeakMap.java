package com.runsidekick.agent.core.util.map;

import com.blogspot.mydailyjava.weaklockfree.WeakConcurrentMap;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe weak {@link ConcurrentMap} implementation.
 *
 * @author serkan
 */
public class ConcurrentWeakMap<K, V> extends WeakConcurrentMap<K, V> {

    private static final int CACHE_CONCURRENCY =
            Math.max(8, Runtime.getRuntime().availableProcessors());

    public ConcurrentWeakMap() {
        this(false);
    }

    public ConcurrentWeakMap(boolean cleanerThread) {
        super(cleanerThread);
    }

    public ConcurrentWeakMap(boolean cleanerThread, boolean reuseKeys) {
        super(cleanerThread, reuseKeys);
    }

    public ConcurrentWeakMap(boolean cleanerThread, boolean reuseKeys, ConcurrentMap<WeakKey<K>, V> target) {
        super(cleanerThread, reuseKeys, target);
    }

    public ConcurrentWeakMap(int maxSize) {
        super(false, true, createMaxSizeMap(maxSize));
    }

    private static ConcurrentMap createMaxSizeMap(int maxSize) {
        return new ConcurrentLinkedHashMap.Builder().
                maximumWeightedCapacity(maxSize).
                concurrencyLevel(CACHE_CONCURRENCY).
                build();
    }

}
