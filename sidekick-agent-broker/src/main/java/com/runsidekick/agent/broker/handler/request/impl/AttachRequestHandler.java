package com.runsidekick.agent.broker.handler.request.impl;

import com.runsidekick.agent.broker.request.impl.AttachRequest;
import com.runsidekick.agent.broker.response.impl.AttachResponse;

/**
 * @author yasin.kalafat
 */
public class AttachRequestHandler extends BaseRequestHandler<AttachRequest, AttachResponse> {

    public static final String REQUEST_NAME = "AttachRequest";

    public AttachRequestHandler() {
        super(REQUEST_NAME, AttachRequest.class, AttachResponse.class);
    }

    @Override
    public AttachResponse handleRequest(AttachRequest request) {
        return null;
    }
}
