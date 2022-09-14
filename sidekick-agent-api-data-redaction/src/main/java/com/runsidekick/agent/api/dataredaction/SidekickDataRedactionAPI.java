package com.runsidekick.agent.api.dataredaction;

import java.util.Map;

/**
 * @author yasin.kalafat
 */
public interface SidekickDataRedactionAPI {

    String redactLogMessage(DataRedactionContext dataRedactionContext, Map<String, String> serializedVariables,
                            String logExpression, String logMessage) throws Exception;

    boolean shouldRedactVariable(DataRedactionContext dataRedactionContext, String fieldName);
}
