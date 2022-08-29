package com.runsidekick.agent.probe.condition.value;

import com.runsidekick.agent.probe.condition.ConditionContext;

/**
 * @author serkan
 */
public interface ValueProvider<V> {

    V getValue(ConditionContext conditionContext);

}
