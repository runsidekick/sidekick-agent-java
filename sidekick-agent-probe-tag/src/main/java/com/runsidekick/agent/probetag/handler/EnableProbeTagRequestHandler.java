package com.runsidekick.agent.probetag.handler;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.handler.request.impl.BaseRequestHandler;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.probetag.request.EnableProbeTagRequest;
import com.runsidekick.agent.probetag.response.EnableTagResponse;
import com.runsidekick.agent.tracepoint.TracePointSupport;

/**
 * @author yasin.kalafat
 */
public class EnableProbeTagRequestHandler extends BaseRequestHandler<EnableProbeTagRequest, EnableTagResponse> {

    public static final String REQUEST_NAME = "EnableProbeTagRequest";

    public EnableProbeTagRequestHandler() {
        super(REQUEST_NAME, EnableProbeTagRequest.class, EnableTagResponse.class);
    }

    @Override
    public EnableTagResponse handleRequest(EnableProbeTagRequest request) {
        try {
            TracePointSupport.enableTag(request.getTag(), request.getClient());
            LogPointSupport.enableTag(request.getTag(), request.getClient());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new EnableTagResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new EnableTagResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }
}
