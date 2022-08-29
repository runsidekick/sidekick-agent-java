package com.runsidekick.agent.tracepoint.handler.request;

import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.tracepoint.request.RemoveBatchTracePointRequest;
import com.runsidekick.agent.tracepoint.response.RemoveBatchTracePointResponse;
import com.runsidekick.agent.broker.BrokerManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author oguzhan
 */
public class RemoveBatchTracePointRequestHandler
        extends BaseTracePointRequestHandler<RemoveBatchTracePointRequest, RemoveBatchTracePointResponse> {

    public static final String REQUEST_NAME = "RemoveBatchTracePointRequest";

    public RemoveBatchTracePointRequestHandler() {
        super(REQUEST_NAME, RemoveBatchTracePointRequest.class, RemoveBatchTracePointResponse.class);
    }

    @Override
    public RemoveBatchTracePointResponse handleRequest(RemoveBatchTracePointRequest request) {
        RemoveBatchTracePointResponse response = new RemoveBatchTracePointResponse();
        List<String> existTracePointIds = TracePointSupport.listTracePointIds(request.getClient());

        List<String> removingTracePointIds = request.getTracePointIds().stream().filter(existTracePointIds::contains)
                .collect(Collectors.toList());
        try {
            removingTracePointIds.stream().forEach(tp -> {
                try {
                    TracePointSupport.removeTracePoint(tp, request.getClient());
                    response.getRemovedTracePointIds().add(tp);
                } catch (Exception e) {
                    response.getUnRemovedTracePointIds().put(tp, e.getMessage());
                }
            });

            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            if (!response.getUnRemovedTracePointIds().isEmpty()) {
                response.setErroneous(true);
            }

            response.setRequestId(request.getId());
            response.setClient(request.getClient());

            return response;
        } catch (Throwable error) {
            return new RemoveBatchTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }

    }
}
