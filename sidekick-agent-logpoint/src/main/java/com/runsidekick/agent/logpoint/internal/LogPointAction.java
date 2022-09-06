package com.runsidekick.agent.logpoint.internal;

import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.broker.error.CommonErrorCodes;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.dataredaction.DataRedactionManager;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.logpoint.event.LogPointEvent;
import com.runsidekick.agent.logpoint.event.LogPointFailedEvent;
import com.runsidekick.agent.logpoint.expression.execute.LogPointExpressionExecutor;
import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.Variable;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yasin
 */
class LogPointAction implements ProbeAction<LogPointContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogPointManager.class);

    public static final String ACTION_ID = "logpoint";

    private final LogPointContext context;
    private final LogPointExpressionExecutor expressionExecutor;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final DataRedactionManager dataRedactionManager;

    LogPointAction(LogPointContext context, LogPointExpressionExecutor expressionExecutor,
                   DataRedactionManager dataRedactionManager) {
        this.context = context;
        this.expressionExecutor = expressionExecutor;
        this.dataRedactionManager = dataRedactionManager;
    }

    @Override
    public String id() {
        return ACTION_ID;
    }

    @Override
    public boolean isDisabled() {
        return context.disabled;
    }

    private void handleOnLogPointEvent(String logPointId, String fileName, String className, int line, String client,
                                       String logExpression, boolean stdoutEnabled, String logLevel,
                                       String methodName, Object callee, String[] localVarNames,
                                       Object[] localVarValues) {
        List<Variable> variableList = new ArrayList<>(localVarNames.length + 1);

        if (callee != null) {
            variableList.add(new Variable("this", callee));
        }

        Map<String, Object> variables = new HashMap<>();
        for (int i = 0; i < localVarNames.length; i++) {
            String localVarName = localVarNames[i];
            Object localVarValue = localVarValues[i];
            variables.put(localVarName, localVarValue);
        }

        Map<String, String> serializedVariables = new LinkedHashMap<>(variableList.size());
        for (Variable variable : variableList) {
            String serializedValue = Variable.VariableSerializer.serializeVariable(variable);
            serializedVariables.put(variable.getName(), serializedValue);
        }

        this.dataRedactionManager.redactFrameData(serializedVariables);

        String logMessage = expressionExecutor.execute(logExpression, serializedVariables);

        logMessage = this.dataRedactionManager.redactLogMessage(fileName, line, methodName,
                serializedVariables, logExpression, logMessage);

        LogPointEvent logPointEvent =
                new LogPointEvent(logPointId,
                        fileName, className, line, methodName, logMessage,
                        dateTimeFormatter.format(LocalDateTime.now()), logLevel);
        logPointEvent.setClient(client);

        if (stdoutEnabled) {
            LogPointSupport.printLogMessage(logLevel, logPointEvent);
        }

        LogPointSupport.publishLogPointEvent(logPointEvent);
    }

    @Override
    public LogPointContext getContext() {
        return context;
    }

    @Override
    public void onProbe(Probe probe,
                        Class<?> clazz, Object obj, String methodName,
                        String[] localVarNames, Object[] localVarValues) {
        try {
            if (LOGGER.isDebugEnabled()) {
                StringBuilder logMessageBuilder = new StringBuilder();
                logMessageBuilder.
                        append("On logpoint in method ").
                        append(probe.getClassName()).
                        append(".").
                        append(methodName).
                        append(" on line ").
                        append(probe.getLineNo()).
                        append("\n");
                for (int i = 0; i < localVarNames.length; i++) {
                    logMessageBuilder.
                            append("\t- ").
                            append(localVarNames[i]).
                            append(": ").
                            append(localVarValues[i]).
                            append("\n");
                }
                LOGGER.debug(logMessageBuilder.toString());
            }

            handleOnLogPointEvent(
                    context.id,
                    probe.getFileName(), probe.getClassName(), probe.getLineNo(), probe.getClient(),
                    context.logExpression, context.stdoutEnabled, context.logLevel,
                    methodName,
                    obj, localVarNames, localVarValues);
        } catch (Throwable t) {
            LOGGER.error(
                    String.format(
                            "Logpoint failed in class %s on line %d from client %s",
                            probe.getClassName(), probe.getLineNo(), probe.getClient()),
                    t);
            int errorCode = CommonErrorCodes.UNKNOWN.getCode();
            if (t instanceof CodedException) {
                errorCode = ((CodedException) t).getCode();
            }
            String errorMessage = t.getMessage();
            LogPointFailedEvent event =
                    new LogPointFailedEvent(
                            probe.getClassName(), probe.getLineNo(), errorCode, errorMessage);
            event.setClient(probe.getClient());
            LogPointSupport.publishLogPointEvent(event);
        }
    }

}
