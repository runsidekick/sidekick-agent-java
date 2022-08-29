package com.runsidekick.agent.tracepoint.handler.request;

import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.tracepoint.request.DisableTracePointRequest;
import com.runsidekick.agent.tracepoint.response.DisableTracePointResponse;
import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.core.util.StringUtils;

/**
 * @author serkan
 */
public class DisableTracePointRequestHandler
        extends BaseTracePointRequestHandler<DisableTracePointRequest, DisableTracePointResponse> {

    public static final String REQUEST_NAME = "DisableTracePointRequest";

    public DisableTracePointRequestHandler() {
        super(REQUEST_NAME, DisableTracePointRequest.class, DisableTracePointResponse.class);
    }

    @Override
    public DisableTracePointResponse handleRequest(DisableTracePointRequest request) {
        try {
            TracePointSupport.disableTracePoint(request.getTracePointId(), request.getClient());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new DisableTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new DisableTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }

}
