package com.runsidekick.agent.broker.handler.response.impl;

import com.runsidekick.agent.broker.response.impl.GetConfigResponse;
import com.runsidekick.agent.core.config.ConfigProvider;

/**
 * @author yasin.kalafat
 */
public class GetConfigResponseHandler extends BaseResponseHandler<GetConfigResponse> {

    public static final String RESPONSE_NAME = "GetConfigResponse";

    public GetConfigResponseHandler() {
        super(RESPONSE_NAME, GetConfigResponse.class);
    }

    @Override
    public void handleResponse(GetConfigResponse response) {
        ConfigProvider.setConfig(response.getConfig());
    }
}
