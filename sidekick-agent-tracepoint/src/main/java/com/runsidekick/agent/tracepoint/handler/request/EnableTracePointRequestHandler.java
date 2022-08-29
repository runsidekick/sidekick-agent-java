package com.runsidekick.agent.tracepoint.handler.request;

import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.tracepoint.request.EnableTracePointRequest;
import com.runsidekick.agent.tracepoint.response.EnableTracePointResponse;
import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.core.util.StringUtils;

/**
 * @author serkan
 */
public class EnableTracePointRequestHandler
        extends BaseTracePointRequestHandler<EnableTracePointRequest, EnableTracePointResponse> {

    public static final String REQUEST_NAME = "EnableTracePointRequest";

    public EnableTracePointRequestHandler() {
        super(REQUEST_NAME, EnableTracePointRequest.class, EnableTracePointResponse.class);
    }

    @Override
    public EnableTracePointResponse handleRequest(EnableTracePointRequest request) {
        try {
            TracePointSupport.enableTracePoint(request.getTracePointId(), request.getClient());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new EnableTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new EnableTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }

}
