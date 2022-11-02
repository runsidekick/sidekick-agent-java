package com.runsidekick.agent.tracepoint.handler.request;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.tracepoint.request.UpdateTracePointRequest;
import com.runsidekick.agent.tracepoint.response.UpdateTracePointResponse;

/**
 * @author serkan
 */
public class UpdateTracePointRequestHandler
        extends BaseTracePointRequestHandler<UpdateTracePointRequest, UpdateTracePointResponse> {

    public static final String REQUEST_NAME = "UpdateTracePointRequest";

    public UpdateTracePointRequestHandler() {
        super(REQUEST_NAME, UpdateTracePointRequest.class, UpdateTracePointResponse.class);
    }

    @Override
    public UpdateTracePointResponse handleRequest(UpdateTracePointRequest request) {
        try {
            TracePointSupport.updateTracePoint(
                    request.getTracePointId(),
                    request.getClient(),
                    request.getConditionExpression(),
                    request.getExpireSecs(),
                    request.getExpireCount(),
                    request.isEnableTracing(),
                    request.isDisable(),
                    request.getTags());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new UpdateTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new UpdateTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }

}
