package com.runsidekick.agent.broker.request.impl;

import com.runsidekick.agent.broker.request.Request;

/**
 * @author serkan
 */
public abstract class BaseRequest implements Request {

    protected String id;
    protected String client;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

}
