package com.runsidekick.agent.logpoint.expression.execute;

import com.runsidekick.agent.api.dataredaction.DataRedactionContext;

import java.util.Map;

/**
 * @author yasin
 */
public interface LogPointExpressionExecutor {

    String execute(DataRedactionContext dataRedactionContext, String expression, Map<String, Object> variables);
}
