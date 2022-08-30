package com.runsidekick.agent.tracepoint.event;

import com.runsidekick.agent.broker.event.impl.BaseEvent;

/**
 * @author serkan
 */
public class TracePointSnapshotFailedEvent extends BaseEvent {

    private final String className;
    private final int lineNo;
    private final int errorCode;
    private final String errorMessage;

    public TracePointSnapshotFailedEvent(String className, int lineNo,
                                         int errorCode, String errorMessage) {
        this.className = className;
        this.lineNo = lineNo;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getClassName() {
        return className;
    }

    public int getLineNo() {
        return lineNo;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "TracePointSnapshotFailedEvent{" +
                "className='" + className + '\'' +
                ", lineNo=" + lineNo +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", id='" + id + '\'' +
                ", sendAck=" + sendAck +
                ", client='" + client + '\'' +
                ", time=" + time +
                ", hostName='" + hostName + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                '}';
    }

}
