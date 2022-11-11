package com.runsidekick.agent.broker.response.impl;

import java.util.Map;

public class GetConfigResponse extends BaseResponse<GetConfigResponse> {

    private Map<String, Object> config;

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

}
