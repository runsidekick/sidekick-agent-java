package com.runsidekick.agent.tracepoint.internal;

import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.broker.error.CommonErrorCodes;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.dataredaction.DataRedactionManager;
import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.Variable;
import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.tracepoint.domain.Frame;
import com.runsidekick.agent.tracepoint.domain.Variables;
import com.runsidekick.agent.tracepoint.event.TracePointSnapshotEvent;
import com.runsidekick.agent.tracepoint.event.TracePointSnapshotFailedEvent;
import com.runsidekick.agent.tracepoint.trace.TraceContext;
import com.runsidekick.agent.tracepoint.trace.TraceSupport;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author serkan
 */
class TracePointAction implements ProbeAction<TracePointContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TracePointManager.class);

    private static final StackTraceProvider stackTraceProvider = new StackTraceProvider();

    public static final String ACTION_ID = "tracepoint";

    private final TracePointContext context;

    private final DataRedactionManager dataRedactionManager;

    TracePointAction(TracePointContext context, DataRedactionManager dataRedactionManager) {
        this.context = context;
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

    private void handleOnTracePointEvent(String tracePointId, Class<?> clazz,
                                         String fileName, String className, int line, String client,
                                         String methodName, boolean enableTracing,
                                         Object callee, String[] localVarNames, Object[] localVarValues) {
        List<Variable> variableList = new ArrayList<>(localVarNames.length + 1);

        if (callee != null) {
            variableList.add(new Variable("this", callee));
        }

        // To get the current state of the local variables,
        // we need to serialize it immediately here, but publish asynchronously
        for (int i = 0; i < localVarNames.length; i++) {
            String localVarName = localVarNames[i];
            Object localVarValue = localVarValues[i];
            Variable variable = new Variable(localVarName, localVarValue);
            variableList.add(variable);
        }
        Map<String, String> serializedVariables = new LinkedHashMap<>(variableList.size());
        for (Variable variable : variableList) {
            String serializedValue = Variable.VariableSerializer.serializeVariable(variable);
            serializedVariables.put(variable.getName(), serializedValue);
        }

        if (enableTracing) {
            // TODO
            // Should we inject local vars into current span as tag?
            // At least for Thundra agent?
        }

        TraceContext traceContext = TraceSupport.getTraceContext(clazz.getClassLoader());
        String traceId = traceContext != null ? traceContext.getTraceId() : null;
        String transactionId = traceContext != null ? traceContext.getTransactionId() : null;
        String spanId = traceContext != null ? traceContext.getSpanId() : null;

        Throwable throwable = new Throwable();

        TracePointSupport.publishTracePointEvent(() -> {

            this.dataRedactionManager.redactFrameData(serializedVariables);

            StackTraceElement[] stackTraceElements = stackTraceProvider.getStackTrace(throwable, 3);
            List<Frame> frames = new ArrayList<>(stackTraceElements.length);

            StackTraceElement tracePointStackTraceElement = stackTraceElements[0];
            Frame tracePointFrame = new Frame(tracePointStackTraceElement, new Variables(serializedVariables));
            frames.add(tracePointFrame);

            for (int i = 1; i < stackTraceElements.length; i++) {
                StackTraceElement stackTraceElement = stackTraceElements[i];
                frames.add(new Frame(stackTraceElement));
            }

            TracePointSnapshotEvent tracePointSnapshotEvent =
                    new TracePointSnapshotEvent(tracePointId,
                            fileName, className, line, methodName, frames,
                            traceId, transactionId, spanId);
            tracePointSnapshotEvent.setClient(client);

            return tracePointSnapshotEvent;
        });
    }

    @Override
    public TracePointContext getContext() {
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
                        append("On tracepoint in method ").
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

            handleOnTracePointEvent(
                    context.id, clazz,
                    probe.getFileName(), probe.getClassName(), probe.getLineNo(), probe.getClient(),
                    methodName, context.enableTracing,
                    obj, localVarNames, localVarValues);
        } catch (Throwable t) {
            LOGGER.error(
                    String.format(
                            "Tracepoint snapshot failed in class %s on line %d from client %s",
                            probe.getClassName(), probe.getLineNo(), probe.getClient()),
                    t);
            int errorCode = CommonErrorCodes.UNKNOWN.getCode();
            if (t instanceof CodedException) {
                errorCode = ((CodedException) t).getCode();
            }
            String errorMessage = t.getMessage();
            TracePointSnapshotFailedEvent event =
                    new TracePointSnapshotFailedEvent(
                            probe.getClassName(), probe.getLineNo(), errorCode, errorMessage);
            event.setClient(probe.getClient());
            TracePointSupport.publishTracePointEvent(event);
        }
    }

}
