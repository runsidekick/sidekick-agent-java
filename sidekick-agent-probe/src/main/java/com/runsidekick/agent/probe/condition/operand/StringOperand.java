package com.runsidekick.agent.probe.condition.operand;

import com.runsidekick.agent.probe.condition.value.ValueProvider;
import com.runsidekick.agent.probe.condition.ConditionContext;

import java.util.Objects;

/**
 * @author serkan
 */
public class StringOperand extends TypedOperand<StringOperand, String> {

    public StringOperand(ValueProvider<String> valueProvider) {
        super(StringOperand.class, String.class, valueProvider);
    }

    @Override
    protected boolean isEQ(String value, ConditionContext conditionContext) {
        String curValue = getValue(conditionContext);
        return Objects.equals(curValue, value);
    }

    @Override
    protected boolean isNE(String value, ConditionContext conditionContext) {
        String curValue = getValue(conditionContext);
        return !Objects.equals(curValue, value);
    }

}
