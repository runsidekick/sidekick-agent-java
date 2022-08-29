package com.runsidekick.agent.probe.condition;

/**
 * @author serkan
 */
public enum ComparisonOperator {

    EQ("=="),
    NE("!="),
    LT("<"),
    LE("<="),
    GT(">"),
    GE(">=");

    private final String expression;

    private ComparisonOperator(String expression) {
        this.expression = expression;
    }

    public static ComparisonOperator fromExpression(String expression) {
        for (ComparisonOperator comparisonOperator : ComparisonOperator.values()) {
            if (comparisonOperator.expression.equals(expression)) {
                return comparisonOperator;
            }
        }
        return null;
    }

}
