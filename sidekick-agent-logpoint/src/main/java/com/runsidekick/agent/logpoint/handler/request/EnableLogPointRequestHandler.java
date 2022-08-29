package com.runsidekick.agent.logpoint.handler.request;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.logpoint.request.EnableLogPointRequest;
import com.runsidekick.agent.logpoint.response.EnableLogPointResponse;

/**
 * @author yasin
 */
public class EnableLogPointRequestHandler
        extends BaseLogPointRequestHandler<EnableLogPointRequest, EnableLogPointResponse> {

    public static final String REQUEST_NAME = "EnableLogPointRequest";

    public EnableLogPointRequestHandler() {
        super(REQUEST_NAME, EnableLogPointRequest.class, EnableLogPointResponse.class);
    }

    @Override
    public EnableLogPointResponse handleRequest(EnableLogPointRequest request) {
        try {
            LogPointSupport.enableLogPoint(request.getLogPointId(), request.getClient());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new EnableLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new EnableLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }

}
