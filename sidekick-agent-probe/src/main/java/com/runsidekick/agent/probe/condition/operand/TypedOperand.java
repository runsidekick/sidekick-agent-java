package com.runsidekick.agent.probe.condition.operand;

import com.runsidekick.agent.probe.condition.ConditionContext;
import com.runsidekick.agent.probe.condition.value.ValueProvider;

/**
 * @author serkan
 */
public abstract class TypedOperand<O extends Operand, V> implements Operand<V> {

    protected final Class<O> operandType;
    protected final Class<V> valueType;
    protected final ValueProvider<V> valueProvider;

    protected TypedOperand(Class<O> operandType, Class<V> valueType,
                           ValueProvider<V> valueProvider) {
        this.operandType = operandType;
        this.valueType = valueType;
        this.valueProvider = valueProvider;
    }

    @Override
    public V getValue(ConditionContext conditionContext) {
        V value = valueProvider.getValue(conditionContext);
        if (value == null || valueType.isAssignableFrom(value.getClass())) {
            return value;
        }
        return null;
    }

    @Override
    public final boolean eq(Operand operand, ConditionContext conditionContext) {
        Object value = operand.getValue(conditionContext);
        if (value == null || valueType.isAssignableFrom(value.getClass())) {
            return isEQ((V) value, conditionContext);
        }
        return false;
    }

    protected boolean isEQ(V value, ConditionContext conditionContext) {
        return false;
    }

    @Override
    public final boolean ne(Operand operand, ConditionContext conditionContext) {
        Object value = operand.getValue(conditionContext);
        if (value == null || valueType.isAssignableFrom(value.getClass())) {
            return isNE((V) value, conditionContext);
        }
        return false;
    }

    protected boolean isNE(V value, ConditionContext conditionContext) {
        return false;
    }

    @Override
    public final boolean lt(Operand operand, ConditionContext conditionContext) {
        Object value = operand.getValue(conditionContext);
        if (value == null || valueType.isAssignableFrom(value.getClass())) {
            return isLT((V) value, conditionContext);
        }
        return false;
    }

    protected boolean isLT(V value, ConditionContext conditionContext) {
        return false;
    }

    @Override
    public final boolean le(Operand operand, ConditionContext conditionContext) {
        Object value = operand.getValue(conditionContext);
        if (value == null || valueType.isAssignableFrom(value.getClass())) {
            return isLE((V) value, conditionContext);
        }
        return false;
    }

    protected boolean isLE(V value, ConditionContext conditionContext) {
        return false;
    }

    @Override
    public final boolean gt(Operand operand, ConditionContext conditionContext) {
        Object value = operand.getValue(conditionContext);
        if (value == null || valueType.isAssignableFrom(value.getClass())) {
            return isGT((V) value, conditionContext);
        }
        return false;
    }

    protected boolean isGT(V value, ConditionContext conditionContext) {
        return false;
    }

    @Override
    public final boolean ge(Operand operand, ConditionContext conditionContext) {
        Object value = operand.getValue(conditionContext);
        if (value == null || valueType.isAssignableFrom(value.getClass())) {
            return isGE((V) value, conditionContext);
        }
        return false;
    }

    protected boolean isGE(V value, ConditionContext conditionContext) {
        return false;
    }

}
