package com.runsidekick.agent.logpoint.expression.execute.impl;

import com.runsidekick.agent.api.dataredaction.DataRedactionContext;
import com.runsidekick.agent.core.util.map.ConcurrentWeakMap;
import com.runsidekick.agent.logpoint.expression.execute.LogPointExpressionExecutor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * @author yasin
 */
public class SpelExpressionExecutor implements LogPointExpressionExecutor {

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private static final ConcurrentWeakMap<String, Expression> expressionMap = new ConcurrentWeakMap();

    @Override
    public String execute(DataRedactionContext dataRedactionContext, String expression, Map<String, Object> variables) {
        // TODO data redaction implementation
        StandardEvaluationContext context = new StandardEvaluationContext(variables);
        context.addPropertyAccessor(new MapAccessor());

        Expression exp = expressionMap.get(expression);
        if (exp == null) {
            exp = parser.parseRaw(expression);
            Expression existingExp = expressionMap.putIfAbsent(expression, exp);
            if (existingExp != null) {
                exp = existingExp;
            }
        }
        return exp.getValue(context, String.class);
    }

}
