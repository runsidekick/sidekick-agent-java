package com.runsidekick.agent.tracepoint.handler.request;

import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.tracepoint.request.RemoveTracePointRequest;
import com.runsidekick.agent.tracepoint.response.RemoveTracePointResponse;
import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.core.util.StringUtils;

/**
 * @author serkan
 */
public class RemoveTracePointRequestHandler
        extends BaseTracePointRequestHandler<RemoveTracePointRequest, RemoveTracePointResponse> {

    public static final String REQUEST_NAME = "RemoveTracePointRequest";

    public RemoveTracePointRequestHandler() {
        super(REQUEST_NAME, RemoveTracePointRequest.class, RemoveTracePointResponse.class);
    }

    @Override
    public RemoveTracePointResponse handleRequest(RemoveTracePointRequest request) {
        try {
            TracePointSupport.removeTracePoint(request.getTracePointId(), request.getClient());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new RemoveTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new RemoveTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }

}
