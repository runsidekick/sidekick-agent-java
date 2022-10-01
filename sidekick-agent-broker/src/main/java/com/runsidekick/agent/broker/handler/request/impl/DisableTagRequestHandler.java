package com.runsidekick.agent.broker.handler.request.impl;

import com.runsidekick.agent.broker.request.impl.DisableTagRequest;
import com.runsidekick.agent.broker.response.impl.DisableTagResponse;

/**
 * @author yasin.kalafat
 */
public class DisableTagRequestHandler extends BaseRequestHandler<DisableTagRequest, DisableTagResponse> {

    public static final String REQUEST_NAME = "DisableTagRequest";

    public DisableTagRequestHandler() {
        super(REQUEST_NAME, DisableTagRequest.class, DisableTagResponse.class);
    }

    @Override
    public DisableTagResponse handleRequest(DisableTagRequest request) {
        return null;
    }
}
