package com.runsidekick.agent.probetag.handler.request;


import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.handler.request.impl.BaseRequestHandler;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.probetag.request.RemoveProbeTagRequest;
import com.runsidekick.agent.probetag.response.RemoveProbeTagResponse;
import com.runsidekick.agent.tracepoint.TracePointSupport;

/**
 * @author yasin.kalafat
 */
public class RemoveProbeTagRequestHandler extends BaseRequestHandler<RemoveProbeTagRequest, RemoveProbeTagResponse> {

    public static final String REQUEST_NAME = "RemoveProbeTagRequest";

    public RemoveProbeTagRequestHandler() {
        super(REQUEST_NAME, RemoveProbeTagRequest.class, RemoveProbeTagResponse.class);
    }

    @Override
    public RemoveProbeTagResponse handleRequest(RemoveProbeTagRequest request) {
        try {
            TracePointSupport.removeTag(request.getTag(), request.getClient());
            LogPointSupport.removeTag(request.getTag(), request.getClient());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new RemoveProbeTagResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new RemoveProbeTagResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }
}
