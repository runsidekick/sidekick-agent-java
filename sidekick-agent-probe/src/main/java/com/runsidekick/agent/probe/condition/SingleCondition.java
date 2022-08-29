package com.runsidekick.agent.probe.condition;

import com.runsidekick.agent.probe.condition.operand.Operand;

/**
 * @author serkan
 */
public class SingleCondition implements Condition {

    private final Operand leftOperand;
    private final Operand rightOperand;
    private final ComparisonOperator comparisonOperator;

    public SingleCondition(Operand leftOperand, Operand rightOperand, ComparisonOperator comparisonOperator) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.comparisonOperator = comparisonOperator;
    }

    @Override
    public boolean evaluate(ConditionContext conditionContext) {
        switch(comparisonOperator) {
            case EQ:
                return leftOperand.eq(rightOperand, conditionContext);
            case NE:
                return leftOperand.ne(rightOperand, conditionContext);
            case LT:
                return leftOperand.lt(rightOperand, conditionContext);
            case LE:
                return leftOperand.le(rightOperand, conditionContext);
            case GT:
                return leftOperand.gt(rightOperand, conditionContext);
            case GE:
                return leftOperand.ge(rightOperand, conditionContext);
            default:
                return false;
        }
    }

}
