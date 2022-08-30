package com.runsidekick.agent.probe.domain;

import com.runsidekick.agent.probe.condition.Condition;

/**
 * @author serkan
 */
public interface ProbeContext {

    default Condition getCondition() {
        return null;
    }

    default int getExpireCount() {
        return -1;
    }

    default void expire() {
    }

}
