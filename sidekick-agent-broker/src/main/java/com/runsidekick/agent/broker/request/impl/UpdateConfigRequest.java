package com.runsidekick.agent.broker.request.impl;

import java.util.Map;

/**
 * @author yasin.kalafat
 */
public class UpdateConfigRequest extends BaseRequest {

    private Map<String, Object> config;

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

}
