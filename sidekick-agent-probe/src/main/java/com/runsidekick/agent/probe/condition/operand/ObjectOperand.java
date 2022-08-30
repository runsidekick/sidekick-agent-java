package com.runsidekick.agent.probe.condition.operand;

import com.runsidekick.agent.probe.condition.value.ValueProvider;
import com.runsidekick.agent.probe.condition.ConditionContext;

import java.util.Objects;

/**
 * @author serkan
 */
public class ObjectOperand<V> implements Operand<V> {

    private final ValueProvider<V> valueProvider;

    public ObjectOperand(ValueProvider<V> valueProvider) {
        this.valueProvider = valueProvider;
    }

    @Override
    public V getValue(ConditionContext conditionContext) {
        return valueProvider.getValue(conditionContext);
    }

    @Override
    public boolean eq(Operand operand, ConditionContext conditionContext) {
        Object value1 = getValue(conditionContext);
        Object value2 = operand.getValue(conditionContext);
        return Objects.equals(value1, value2);
    }

    @Override
    public boolean ne(Operand operand, ConditionContext conditionContext) {
        Object value1 = getValue(conditionContext);
        Object value2 = operand.getValue(conditionContext);
        return !Objects.equals(value1, value2);
    }

    @Override
    public boolean lt(Operand operand, ConditionContext conditionContext) {
        return false;
    }

    @Override
    public boolean le(Operand operand, ConditionContext conditionContext) {
        return false;
    }

    @Override
    public boolean gt(Operand operand, ConditionContext conditionContext) {
        return false;
    }

    @Override
    public boolean ge(Operand operand, ConditionContext conditionContext) {
        return false;
    }

}
