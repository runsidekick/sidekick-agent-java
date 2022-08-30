package com.runsidekick.agent.logpoint.event;

import com.runsidekick.agent.broker.event.impl.BaseEvent;

/**
 * @author yasin
 */
public class LogPointEvent extends BaseEvent {

    private final String logPointId;
    private final String fileName;
    private final String className;
    private final int lineNo;
    private final String methodName;
    private final String logMessage;
    private final String createdAt;
    private final String logLevel;

    public LogPointEvent(String logPointId, String fileName, String className,
                         int lineNo, String methodName, String logMessage, String createdAt, String logLevel) {
        this.logPointId = logPointId;
        this.fileName = fileName;
        this.className = className;
        this.lineNo = lineNo;
        this.methodName = methodName;
        this.logMessage = logMessage;
        this.createdAt = createdAt;
        this.logLevel = logLevel;
    }

    public String getLogPointId() {
        return logPointId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getClassName() {
        return className;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getLogLevel() {
        return logLevel;
    }

    @Override
    public String toString() {
        return "LogPointEvent{" +
                "logPointId='" + logPointId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", lineNo=" + lineNo +
                ", methodName='" + methodName + '\'' +
                ", id='" + id + '\'' +
                ", sendAck=" + sendAck +
                ", client='" + client + '\'' +
                ", time=" + time +
                ", hostName='" + hostName + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", logMessage='" + logMessage + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", logLevel='" + logLevel + '\'' +
                '}';
    }

}
