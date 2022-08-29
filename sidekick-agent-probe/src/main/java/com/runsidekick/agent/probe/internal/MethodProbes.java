package com.runsidekick.agent.probe.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author serkan
 */
class MethodProbes {

    final Map<String, InternalProbe> probes = new ConcurrentHashMap<>();
    final ClassProbes ownerClassProbes;
    final String methodId;

    MethodProbes(ClassProbes ownerClassProbes, String methodId) {
        this.ownerClassProbes = ownerClassProbes;
        this.methodId = methodId;
    }

}
