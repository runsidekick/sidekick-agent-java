package com.runsidekick.agent.broker.event.impl;

import com.runsidekick.agent.broker.event.MutableEvent;

/**
 * @author serkan
 */
public abstract class BaseEvent implements MutableEvent {

    protected String id;
    protected boolean sendAck;
    protected String client;
    protected long time;
    protected String hostName;
    protected String applicationName;
    protected String applicationInstanceId;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isSendAck() {
        return sendAck;
    }

    public void setSendAck(boolean sendAck) {
        this.sendAck = sendAck;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getClient() {
        return client;
    }

    @Override
    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String getApplicationInstanceId() {
        return applicationInstanceId;
    }

    @Override
    public void setApplicationInstanceId(String applicationInstanceId) {
        this.applicationInstanceId = applicationInstanceId;
    }

}
