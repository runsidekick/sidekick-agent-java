package com.runsidekick.agent.broker.error;

/**
 * @author serkan
 */
public interface CodedError {

    int getCode();
    String getMessageTemplate();
    String formatMessage(Object... args);

}
