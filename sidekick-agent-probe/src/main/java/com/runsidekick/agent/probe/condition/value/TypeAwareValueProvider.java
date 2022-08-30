package com.runsidekick.agent.probe.condition.value;

/**
 * @author serkan
 */
public interface TypeAwareValueProvider<V> extends ValueProvider<V> {

    Class<V> getValueType();

}
