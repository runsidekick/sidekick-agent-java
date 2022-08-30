package com.runsidekick.agent.probe.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author serkan
 */
class ClassProbes {

    final Map<String, MethodProbes> methodProbesMap = new ConcurrentHashMap<>();

    ClassProbes() {
    }

}
