package com.runsidekick.agent.broker.event.impl;

import com.runsidekick.agent.broker.domain.ApplicationStatus;

/**
 * @author serkan
 */
public class ApplicationStatusEvent extends BaseEvent {

    private final ApplicationStatus application;

    public ApplicationStatusEvent(ApplicationStatus application) {
        this.application = application;
    }

    public ApplicationStatusEvent(ApplicationStatus application, String client) {
        this.application = application;
        this.client = client;
    }

    public ApplicationStatus getApplication() {
        return application;
    }

    @Override
    public String toString() {
        return "ApplicationStatusEvent{" +
                "application=" + application +
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
