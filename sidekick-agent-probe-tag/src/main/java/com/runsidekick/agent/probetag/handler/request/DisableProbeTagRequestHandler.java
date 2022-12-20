package com.runsidekick.agent.probetag.handler.request;


import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.handler.request.impl.BaseRequestHandler;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.probetag.request.DisableProbeTagRequest;
import com.runsidekick.agent.probetag.response.DisableProbeTagResponse;
import com.runsidekick.agent.tracepoint.TracePointSupport;

/**
 * @author yasin.kalafat
 */
public class DisableProbeTagRequestHandler extends BaseRequestHandler<DisableProbeTagRequest, DisableProbeTagResponse> {

    public static final String REQUEST_NAME = "DisableProbeTagRequest";

    public DisableProbeTagRequestHandler() {
        super(REQUEST_NAME, DisableProbeTagRequest.class, DisableProbeTagResponse.class);
    }

    @Override
    public DisableProbeTagResponse handleRequest(DisableProbeTagRequest request) {
        try {
            TracePointSupport.disableTag(request.getTag(), request.getClient());
            LogPointSupport.disableTag(request.getTag(), request.getClient());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new DisableProbeTagResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new DisableProbeTagResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }
}
