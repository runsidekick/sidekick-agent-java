package com.runsidekick.agent.probe;

import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.broker.error.CommonErrorCodes;
import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.probe.event.ProbeActionFailedEvent;
import org.slf4j.Logger;

/**
 * @author serkan
 */
public final class ProbeBridge {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProbeBridge.class);

    private ProbeBridge() {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void onProbe(String probeId, Class<?> clazz, Object obj,
                               String[] localVarNames, Object[] localVarValues) {
        Probe probe = ProbeSupport.getProbe(probeId);
        if (probe == null || probe.isRemoved()) {
            LOGGER.debug("No probe could be found with id {}", probeId);
            return;
        }
        try {
            if (LOGGER.isDebugEnabled()) {
                logProbe(probe, clazz, obj,localVarNames, localVarValues);
            }

            handleOnProbe(probe, clazz, obj, localVarNames, localVarValues);
        } catch (Throwable t) {
            LOGGER.error(
                    String.format(
                        "Probe handling failed in class %s on line %d from client %s",
                        probe.getClassName(), probe.getLineNo(), probe.getClient()),
                    t);
        }
    }

    private static void logProbe(Probe probe, Class<?> clazz, Object object,
                                 String[] localVarNames, Object[] localVarValues) {
        StringBuilder logMessageBuilder = new StringBuilder();
        logMessageBuilder.
                append("On probe in class ").
                append(probe.getClassName()).
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

    private static void handleOnProbe(Probe probe, Class<?> clazz, Object object,
                                      String[] localVarNames, Object[] localVarValues) {
        for (ProbeAction action : probe.actions()) {
            LOGGER.debug(
                    "Running action {} on probe in class {} on line {} from client {} ...",
                    action, probe.getClassName(), probe.getLineNo(), probe.getClient());
            try {
                if (action.isDisabled()) {
                    LOGGER.debug(
                            "Skipping action {} on probe in class {} on line {} from client {} as it is disabled",
                            action, probe.getClassName(), probe.getLineNo(), probe.getClient());
                    continue;
                }

                action.onProbe(probe, clazz, object, probe.getMethodName(), localVarNames, localVarValues);
            } catch (Throwable t) {
                LOGGER.error(
                        String.format(
                            "Error occurred while running action %s on probe in class %s on line %d from client %s",
                            action, probe.getClassName(), probe.getLineNo(), probe.getClient()),
                        t);
                int errorCode = CommonErrorCodes.UNKNOWN.getCode();
                if (t instanceof CodedException) {
                    errorCode = ((CodedException) t).getCode();
                }
                String errorMessage = t.getMessage();
                ProbeActionFailedEvent event =
                        new ProbeActionFailedEvent(
                                probe.getClassName(), probe.getLineNo(), probe.getClient(),
                                errorCode, errorMessage);
                ProbeSupport.publishProbeEvent(event);
            }
        }
    }

}
