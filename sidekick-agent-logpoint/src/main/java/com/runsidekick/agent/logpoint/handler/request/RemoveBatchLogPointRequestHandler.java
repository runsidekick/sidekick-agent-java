package com.runsidekick.agent.logpoint.handler.request;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.logpoint.request.RemoveBatchLogPointRequest;
import com.runsidekick.agent.logpoint.response.RemoveBatchLogPointResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yasin
 */
public class RemoveBatchLogPointRequestHandler
        extends BaseLogPointRequestHandler<RemoveBatchLogPointRequest, RemoveBatchLogPointResponse> {

    public static final String REQUEST_NAME = "RemoveBatchLogPointRequest";

    public RemoveBatchLogPointRequestHandler() {
        super(REQUEST_NAME, RemoveBatchLogPointRequest.class, RemoveBatchLogPointResponse.class);
    }

    @Override
    public RemoveBatchLogPointResponse handleRequest(RemoveBatchLogPointRequest request) {
        RemoveBatchLogPointResponse response = new RemoveBatchLogPointResponse();
        List<String> existLogPointIds = LogPointSupport.listLogPointIds(request.getClient());

        List<String> removingLogPointIds = request.getLogPointIds().stream().filter(existLogPointIds::contains)
                .collect(Collectors.toList());
        try {
            removingLogPointIds.stream().forEach(tp -> {
                try {
                    LogPointSupport.removeLogPoint(tp, request.getClient());
                    response.getRemovedLogPointIds().add(tp);
                } catch (Exception e) {
                    response.getUnRemovedLogPointIds().put(tp, e.getMessage());
                }
            });

            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            if (!response.getUnRemovedLogPointIds().isEmpty()) {
                response.setErroneous(true);
            }

            response.setRequestId(request.getId());
            response.setClient(request.getClient());

            return response;
        } catch (Throwable error) {
            return new RemoveBatchLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }

    }
}
