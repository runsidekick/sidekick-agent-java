package com.runsidekick.agent.broker.handler.request.impl;

import com.runsidekick.agent.broker.BrokerManager;
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
        try {
            BrokerManager.attach();

            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new AttachResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new AttachResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }
}
