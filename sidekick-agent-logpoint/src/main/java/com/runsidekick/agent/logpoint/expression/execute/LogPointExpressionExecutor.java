package com.runsidekick.agent.logpoint.expression.execute;

import java.util.Map;

/**
 * @author yasin
 */
public interface LogPointExpressionExecutor {

    String execute(String expression, Map<String, Object> variables);
}
