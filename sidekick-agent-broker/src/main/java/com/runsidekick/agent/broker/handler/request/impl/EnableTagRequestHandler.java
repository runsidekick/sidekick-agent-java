package com.runsidekick.agent.broker.handler.request.impl;

import com.runsidekick.agent.broker.request.impl.EnableTagRequest;
import com.runsidekick.agent.broker.response.impl.EnableTagResponse;

/**
 * @author yasin.kalafat
 */
public class EnableTagRequestHandler extends BaseRequestHandler<EnableTagRequest, EnableTagResponse> {

    public static final String REQUEST_NAME = "EnableTagRequest";

    public EnableTagRequestHandler() {
        super(REQUEST_NAME, EnableTagRequest.class, EnableTagResponse.class);
    }

    @Override
    public EnableTagResponse handleRequest(EnableTagRequest request) {
        return null;
    }
}
