package com.runsidekick.agent.probe.condition.operand;

import com.runsidekick.agent.probe.condition.value.ValueProvider;
import com.runsidekick.agent.probe.condition.ConditionContext;

import java.util.Objects;

/**
 * @author serkan
 */
public class NumberOperand extends TypedOperand<NumberOperand, Number> {

    public NumberOperand(ValueProvider<Number> valueProvider) {
        super(NumberOperand.class, Number.class, valueProvider);
    }

    @Override
    protected boolean isEQ(Number value, ConditionContext conditionContext) {
        Number curValue = getValue(conditionContext);
        if (curValue == null || value == null) {
            return curValue == value;
        }
        return Objects.equals(curValue.doubleValue(), value.doubleValue());
    }

    @Override
    protected boolean isNE(Number value, ConditionContext conditionContext) {
        Number curValue = getValue(conditionContext);
        if (curValue == null || value == null) {
            return curValue != value;
        }
        return !Objects.equals(curValue.doubleValue(), value.doubleValue());
    }

    @Override
    protected boolean isLT(Number value, ConditionContext conditionContext) {
        Number curValue = getValue(conditionContext);
        if (curValue == null || value == null) {
            return false;
        }
        return curValue.doubleValue() < value.doubleValue();
    }

    @Override
    protected boolean isLE(Number value, ConditionContext conditionContext) {
        Number curValue = getValue(conditionContext);
        if (curValue == null || value == null) {
            return false;
        }
        return curValue.doubleValue() <= value.doubleValue();
    }

    @Override
    protected boolean isGT(Number value, ConditionContext conditionContext) {
        Number curValue = getValue(conditionContext);
        if (curValue == null || value == null) {
            return false;
        }
        return curValue.doubleValue() > value.doubleValue();
    }

    @Override
    protected boolean isGE(Number value, ConditionContext conditionContext) {
        Number curValue = getValue(conditionContext);
        if (curValue == null || value == null) {
            return false;
        }
        return curValue.doubleValue() >= value.doubleValue();
    }

}
