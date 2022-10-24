package com.runsidekick.agent.broker.handler.request.impl;

import com.runsidekick.agent.broker.request.impl.UpdateConfigRequest;
import com.runsidekick.agent.broker.response.impl.UpdateConfigResponse;

/**
 * @author yasin.kalafat
 */
public class UpdateConfigRequestHandler extends BaseRequestHandler<UpdateConfigRequest, UpdateConfigResponse> {

    public static final String REQUEST_NAME = "UpdateConfigRequest";

    public UpdateConfigRequestHandler() {
        super(REQUEST_NAME, UpdateConfigRequest.class, UpdateConfigResponse.class);
    }

    @Override
    public UpdateConfigResponse handleRequest(UpdateConfigRequest request) {
        return null;
    }
}
