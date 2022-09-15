package com.runsidekick.agent.api.dataredaction;

/**
 * @author yasin.kalafat
 */
public interface SidekickDataRedactionAPI {

    default String redactLogMessage(DataRedactionContext dataRedactionContext,
                                    String logExpression, String logMessage) {
        return logMessage;
    }

    boolean shouldRedactVariable(DataRedactionContext dataRedactionContext, String fieldName);
}
