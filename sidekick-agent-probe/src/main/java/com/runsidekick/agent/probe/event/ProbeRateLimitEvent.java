package com.runsidekick.agent.probe.event;

import com.runsidekick.agent.broker.event.impl.BaseEvent;

/**
 * @author serkan
 */
public class ProbeRateLimitEvent extends BaseEvent {

    private final String className;
    private final int lineNo;

    public ProbeRateLimitEvent(String className, int lineNo, String client) {
        this.className = className;
        this.lineNo = lineNo;
        this.client = client;
    }

    public String getClassName() {
        return className;
    }

    public int getLineNo() {
        return lineNo;
    }

    @Override
    public String toString() {
        return "ProbeRateLimitEvent{" +
                "className='" + className + '\'' +
                ", lineNo=" + lineNo +
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
