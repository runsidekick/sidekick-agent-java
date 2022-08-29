package com.runsidekick.agent.core.util.map;

import java.util.WeakHashMap;

/**
 * Non-thread-safe weak {@link java.util.Map} implementation.
 *
 * @author serkan
 */
public class WeakMap<K, V> extends WeakHashMap<K, V> {

    public WeakMap() {
    }

    public WeakMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public WeakMap(int initialCapacity) {
        super(initialCapacity);
    }

}
