package com.runsidekick.agent.probe.condition.operand;

import com.runsidekick.agent.probe.condition.ConditionUtils;
import com.runsidekick.agent.probe.condition.VariableInfo;
import com.runsidekick.agent.probe.condition.VariableInfoProvider;
import com.runsidekick.agent.probe.condition.value.VariableValueProvider;
import com.runsidekick.agent.probe.condition.ConditionContext;
import com.runsidekick.agent.probe.error.ProbeErrorCodes;
import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.core.util.StringUtils;

/**
 * @author serkan
 */
public class VariableOperand<T> implements Operand<T> {

    private static final NullOperand NULL_OPERAND = new NullOperand();

    private final String variableName;
    private final VariableValueProvider variableValueProvider;
    private final String propPath;
    private final Operand<T> variableOperand;

    public VariableOperand(String variableName, VariableInfoProvider variableInfoProvider) {
        this(variableName, variableInfoProvider, null);
    }

    public VariableOperand(String variableName, VariableInfoProvider variableInfoProvider,
                           String propPath) {
        this.variableName = variableName;
        this.propPath = propPath;
        VariableInfo variableInfo = variableInfoProvider.getVariableInfo(variableName);
        if (variableInfo == null) {
            throw new CodedException(
                    ProbeErrorCodes.UNABLE_TO_FIND_TYPE_OF_VARIABLE_FOR_CONDITION, variableName);
        }
        Class variableType = variableInfo.getType();
        if (variableType == null) {
            throw new CodedException(
                    ProbeErrorCodes.UNABLE_TO_FIND_METADATA_OF_VARIABLE_FOR_CONDITION, variableName);
        }
        this.variableValueProvider = new VariableValueProvider<>(variableInfo.getIndex(), propPath);
        this.variableOperand = createVariableOperand(variableType, variableValueProvider, propPath);
    }

    private static Operand createVariableOperand(Class variableType,
                                                 VariableValueProvider variableValueProvider,
                                                 String propPath) {
        if (ConditionUtils.isBooleanType(variableType)) {
            return new BooleanOperand(variableValueProvider);
        } else if (ConditionUtils.isCharacterType(variableType)) {
            return new CharacterOperand(variableValueProvider);
        } else if (ConditionUtils.isNumberType(variableType)) {
            return new NumberOperand(variableValueProvider);
        } else if (ConditionUtils.isStringType(variableType)) {
            return new StringOperand(variableValueProvider);
        } else if (StringUtils.isNullOrEmpty(propPath)) {
            return new ObjectOperand(variableValueProvider);
        } else {
            return null;
        }
    }

    public String getVariableName() {
        return variableName;
    }

    public String getPropPath() {
        return propPath;
    }

    @Override
    public T getValue(ConditionContext conditionContext) {
        return (T) variableValueProvider.getValue(conditionContext);
    }

    private Operand getVariableOperand(ConditionContext conditionContext) {
        if (variableOperand != null) {
            return variableOperand;
        }
        Object variableValue = variableValueProvider.getValue(conditionContext);
        if (variableValue == null) {
            return NULL_OPERAND;
        }
        // TODO Cache variable operand
        return createVariableOperand(variableValue.getClass(), variableValueProvider, null);
    }

    @Override
    public boolean eq(Operand operand, ConditionContext conditionContext) {
        Operand varOperand = getVariableOperand(conditionContext);
        if (varOperand == null) {
            return false;
        }
        return varOperand.eq(operand, conditionContext);
    }

    @Override
    public boolean ne(Operand operand, ConditionContext conditionContext) {
        Operand varOperand = getVariableOperand(conditionContext);
        if (varOperand == null) {
            return false;
        }
        return varOperand.ne(operand, conditionContext);
    }

    @Override
    public boolean lt(Operand operand, ConditionContext conditionContext) {
        Operand varOperand = getVariableOperand(conditionContext);
        if (varOperand == null) {
            return false;
        }
        return varOperand.lt(operand, conditionContext);
    }

    @Override
    public boolean le(Operand operand, ConditionContext conditionContext) {
        Operand varOperand = getVariableOperand(conditionContext);
        if (varOperand == null) {
            return false;
        }
        return varOperand.le(operand, conditionContext);
    }

    @Override
    public boolean gt(Operand operand, ConditionContext conditionContext) {
        Operand varOperand = getVariableOperand(conditionContext);
        if (varOperand == null) {
            return false;
        }
        return varOperand.gt(operand, conditionContext);
    }

    @Override
    public boolean ge(Operand operand, ConditionContext conditionContext) {
        Operand varOperand = getVariableOperand(conditionContext);
        if (varOperand == null) {
            return false;
        }
        return varOperand.ge(operand, conditionContext);
    }

}
