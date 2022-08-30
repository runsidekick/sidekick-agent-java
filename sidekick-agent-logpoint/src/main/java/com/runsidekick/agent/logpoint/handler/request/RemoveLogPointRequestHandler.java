package com.runsidekick.agent.logpoint.handler.request;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.logpoint.request.RemoveLogPointRequest;
import com.runsidekick.agent.logpoint.response.RemoveLogPointResponse;

/**
 * @author yasin
 */
public class RemoveLogPointRequestHandler
        extends BaseLogPointRequestHandler<RemoveLogPointRequest, RemoveLogPointResponse> {

    public static final String REQUEST_NAME = "RemoveLogPointRequest";

    public RemoveLogPointRequestHandler() {
        super(REQUEST_NAME, RemoveLogPointRequest.class, RemoveLogPointResponse.class);
    }

    @Override
    public RemoveLogPointResponse handleRequest(RemoveLogPointRequest request) {
        try {
            LogPointSupport.removeLogPoint(request.getLogPointId(), request.getClient());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new RemoveLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new RemoveLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }

}
