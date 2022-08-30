package com.runsidekick.agent.logpoint.handler.request;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.logpoint.request.DisableLogPointRequest;
import com.runsidekick.agent.logpoint.response.DisableLogPointResponse;

/**
 * @author yasin
 */
public class DisableLogPointRequestHandler
        extends BaseLogPointRequestHandler<DisableLogPointRequest, DisableLogPointResponse> {

    public static final String REQUEST_NAME = "DisableLogPointRequest";

    public DisableLogPointRequestHandler() {
        super(REQUEST_NAME, DisableLogPointRequest.class, DisableLogPointResponse.class);
    }

    @Override
    public DisableLogPointResponse handleRequest(DisableLogPointRequest request) {
        try {
            LogPointSupport.disableLogPoint(request.getLogPointId(), request.getClient());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new DisableLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new DisableLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }

}
