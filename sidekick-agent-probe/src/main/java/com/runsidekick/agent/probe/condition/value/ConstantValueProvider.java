package com.runsidekick.agent.probe.condition.value;

import com.runsidekick.agent.probe.condition.ConditionContext;

/**
 * @author serkan
 */
public class ConstantValueProvider<V> implements ValueProvider<V> {

    private final V value;

    public ConstantValueProvider(V value) {
        this.value = value;
    }

    @Override
    public V getValue(ConditionContext conditionContext) {
        return value;
    }

}
