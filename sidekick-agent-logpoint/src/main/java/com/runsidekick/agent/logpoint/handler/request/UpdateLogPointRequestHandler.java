package com.runsidekick.agent.logpoint.handler.request;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.logpoint.request.UpdateLogPointRequest;
import com.runsidekick.agent.logpoint.response.UpdateLogPointResponse;

/**
 * @author yasin
 */
public class UpdateLogPointRequestHandler
        extends BaseLogPointRequestHandler<UpdateLogPointRequest, UpdateLogPointResponse> {

    public static final String REQUEST_NAME = "UpdateLogPointRequest";

    public UpdateLogPointRequestHandler() {
        super(REQUEST_NAME, UpdateLogPointRequest.class, UpdateLogPointResponse.class);
    }

    @Override
    public UpdateLogPointResponse handleRequest(UpdateLogPointRequest request) {
        try {
            LogPointSupport.updateLogPoint(
                    request.getLogPointId(),
                    request.getClient(),
                    request.getLogExpression(),
                    request.getConditionExpression(),
                    request.getExpireSecs(),
                    request.getExpireCount(),
                    request.isDisable(),
                    request.isStdoutEnabled(),
                    request.getLogLevel(),
                    request.getTags());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new UpdateLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new UpdateLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }

}
