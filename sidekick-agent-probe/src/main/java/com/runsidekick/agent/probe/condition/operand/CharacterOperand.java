package com.runsidekick.agent.probe.condition.operand;

import com.runsidekick.agent.probe.condition.value.ValueProvider;
import com.runsidekick.agent.probe.condition.ConditionContext;

import java.util.Objects;

/**
 * @author serkan
 */
public class CharacterOperand extends TypedOperand<CharacterOperand, Character> {

    public CharacterOperand(ValueProvider<Character> valueProvider) {
        super(CharacterOperand.class, Character.class, valueProvider);
    }

    @Override
    protected boolean isEQ(Character value, ConditionContext conditionContext) {
        Character curValue = getValue(conditionContext);
        return Objects.equals(curValue, value);
    }

    @Override
    protected boolean isNE(Character value, ConditionContext conditionContext) {
        Character curValue = getValue(conditionContext);
        return !Objects.equals(curValue, value);
    }

}
