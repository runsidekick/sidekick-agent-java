package com.runsidekick.agent.probe.condition.operand;

import com.runsidekick.agent.probe.condition.ConditionContext;

/**
 * @author serkan
 */
public interface Operand<V> {

    V getValue(ConditionContext conditionContext);

    default boolean eq(Operand operand, ConditionContext conditionContext) {
        return false;
    }

    default boolean ne(Operand operand, ConditionContext conditionContext) {
        return false;
    }

    default boolean lt(Operand operand, ConditionContext conditionContext) {
        return false;
    }

    default boolean le(Operand operand, ConditionContext conditionContext) {
        return false;
    }

    default boolean gt(Operand operand, ConditionContext conditionContext) {
        return false;
    }

    default boolean ge(Operand operand, ConditionContext conditionContext) {
        return false;
    }

}
