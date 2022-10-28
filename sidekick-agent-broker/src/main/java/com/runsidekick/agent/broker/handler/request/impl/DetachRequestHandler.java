package com.runsidekick.agent.broker.handler.request.impl;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.request.impl.DetachRequest;
import com.runsidekick.agent.broker.response.impl.DetachResponse;

/**
 * @author yasin.kalafat
 */
public class DetachRequestHandler extends BaseRequestHandler<DetachRequest, DetachResponse> {

    public static final String REQUEST_NAME = "DetachRequest";

    public DetachRequestHandler() {
        super(REQUEST_NAME, DetachRequest.class, DetachResponse.class);
    }

    @Override
    public DetachResponse handleRequest(DetachRequest request) {
        try {
            BrokerManager.detach();
            return new DetachResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new DetachResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }
}
