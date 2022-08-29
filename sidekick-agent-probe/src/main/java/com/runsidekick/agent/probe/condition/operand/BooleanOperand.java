package com.runsidekick.agent.probe.condition.operand;

import com.runsidekick.agent.probe.condition.value.ValueProvider;
import com.runsidekick.agent.probe.condition.ConditionContext;

import java.util.Objects;

/**
 * @author serkan
 */
public class BooleanOperand extends TypedOperand<BooleanOperand, Boolean> {

    public BooleanOperand(ValueProvider<Boolean> valueProvider) {
        super(BooleanOperand.class, Boolean.class, valueProvider);
    }

    @Override
    protected boolean isEQ(Boolean value, ConditionContext conditionContext) {
        Boolean curValue = getValue(conditionContext);
        return Objects.equals(curValue, value);
    }

    @Override
    protected boolean isNE(Boolean value, ConditionContext conditionContext) {
        Boolean curValue = getValue(conditionContext);
        return !Objects.equals(curValue, value);
    }

}
