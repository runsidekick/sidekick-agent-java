package com.runsidekick.agent.api.dataredaction;

import java.util.Map;

/**
 * @author yasin.kalafat
 */
public interface SidekickDataRedactionAPI {

    Object redactVariableValue(DataRedactionContext dataRedactionContext, String varName, Object varValue)
            throws Exception;

    String redactLogMessage(DataRedactionContext dataRedactionContext, Map<String, String> serializedVariables,
                            String logExpression, String logMessage) throws Exception;

}
