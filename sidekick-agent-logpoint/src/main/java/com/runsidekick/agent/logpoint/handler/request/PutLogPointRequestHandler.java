package com.runsidekick.agent.logpoint.handler.request;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.logpoint.request.PutLogPointRequest;
import com.runsidekick.agent.logpoint.response.PutLogPointResponse;

/**
 * @author yasin
 */
public class PutLogPointRequestHandler
        extends BaseLogPointRequestHandler<PutLogPointRequest, PutLogPointResponse> {

    public static final String REQUEST_NAME = "PutLogPointRequest";

    public PutLogPointRequestHandler() {
        super(REQUEST_NAME, PutLogPointRequest.class, PutLogPointResponse.class);
    }

    @Override
    public PutLogPointResponse handleRequest(PutLogPointRequest request) {
        try {
            String className =
                    validateAndGetClassName(request.getClassName(), request.getFileName(), request.getLineNo());

            LogPointSupport.putLogPoint(
                    request.getLogPointId(),
                    request.getFileName(),
                    className,
                    request.getLineNo(),
                    request.getClient(),
                    request.getLogExpression(),
                    request.getFileHash(),
                    request.getConditionExpression(),
                    request.getExpireSecs(),
                    request.getExpireCount(),
                    request.isStdoutEnabled(),
                    request.getLogLevel(),
                    request.isDisable());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new PutLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new PutLogPointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }

}
