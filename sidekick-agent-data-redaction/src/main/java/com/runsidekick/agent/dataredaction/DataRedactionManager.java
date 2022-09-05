package com.runsidekick.agent.dataredaction;

import java.util.Map;

/**
 * @author yasin.kalafat
 */
public interface DataRedactionManager {

    void redactFrameData(Map<String, String> serializedVariables);

    String redactLogMessage(String fileName, int lineNo, String methodName,
                            Map<String, String> serializedVariables, String logExpression, String logMessage);

}
