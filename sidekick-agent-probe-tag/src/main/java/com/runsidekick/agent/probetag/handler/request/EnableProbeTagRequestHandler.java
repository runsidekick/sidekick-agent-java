package com.runsidekick.agent.probetag.handler.request;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.handler.request.impl.BaseRequestHandler;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.probetag.request.EnableProbeTagRequest;
import com.runsidekick.agent.probetag.response.EnableProbeTagResponse;
import com.runsidekick.agent.tracepoint.TracePointSupport;

/**
 * @author yasin.kalafat
 */
public class EnableProbeTagRequestHandler extends BaseRequestHandler<EnableProbeTagRequest, EnableProbeTagResponse> {

    public static final String REQUEST_NAME = "EnableProbeTagRequest";

    public EnableProbeTagRequestHandler() {
        super(REQUEST_NAME, EnableProbeTagRequest.class, EnableProbeTagResponse.class);
    }

    @Override
    public EnableProbeTagResponse handleRequest(EnableProbeTagRequest request) {
        try {
            System.out.println(request.toString());
            TracePointSupport.enableTag(request.getTag(), request.getClient());
            LogPointSupport.enableTag(request.getTag(), request.getClient());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new EnableProbeTagResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new EnableProbeTagResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }
}
