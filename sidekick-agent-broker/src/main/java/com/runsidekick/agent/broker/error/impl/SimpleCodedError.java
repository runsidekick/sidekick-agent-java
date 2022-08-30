package com.runsidekick.agent.broker.error.impl;

import com.runsidekick.agent.broker.error.CodedError;

/**
 * @author serkan
 */
public class SimpleCodedError implements CodedError {

    protected final int code;
    protected final String messageTemplate;

    public SimpleCodedError(int code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }

    @Override
    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }

}
