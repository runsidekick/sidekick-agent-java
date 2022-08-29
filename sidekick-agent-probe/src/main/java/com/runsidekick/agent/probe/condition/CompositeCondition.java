package com.runsidekick.agent.probe.condition;

import java.util.List;

/**
 * @author serkan
 */
public class CompositeCondition implements Condition {

    private final List<Condition> conditions;
    private final List<BinaryOperator> binaryOperators;

    public CompositeCondition(List<Condition> conditions, List<BinaryOperator> binaryOperators) {
        this.conditions = conditions;
        this.binaryOperators = binaryOperators;
    }

    @Override
    public boolean evaluate(ConditionContext conditionContext) {
        Boolean result = null;
        for (int i = 0; i < conditions.size(); i++) {
            Condition condition = conditions.get(i);
            boolean evaluationResult = condition.evaluate(conditionContext);
            if (result == null) {
                result = evaluationResult;
            } else {
                BinaryOperator binaryOperator = binaryOperators.get(i - 1);
                switch (binaryOperator) {
                    case AND:
                        result = result && evaluationResult;
                        break;
                    case OR:
                        result = result || evaluationResult;
                        break;
                }
            }
        }
        if (result != null) {
            return result;
        } else {
            return false;
        }
    }

}
