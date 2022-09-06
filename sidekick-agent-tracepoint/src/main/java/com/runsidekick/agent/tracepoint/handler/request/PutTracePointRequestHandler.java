package com.runsidekick.agent.tracepoint.handler.request;

import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.tracepoint.request.PutTracePointRequest;
import com.runsidekick.agent.tracepoint.response.PutTracePointResponse;
import com.runsidekick.agent.broker.BrokerManager;

/**
 * @author serkan
 */
public class PutTracePointRequestHandler
        extends BaseTracePointRequestHandler<PutTracePointRequest, PutTracePointResponse> {

    public static final String REQUEST_NAME = "PutTracePointRequest";

    public PutTracePointRequestHandler() {
        super(REQUEST_NAME, PutTracePointRequest.class, PutTracePointResponse.class);
    }

    @Override
    public PutTracePointResponse handleRequest(PutTracePointRequest request) {
        try {
            String className =
                    validateAndGetClassName(request.getClassName(), request.getFileName(), request.getLineNo());

            TracePointSupport.putTracePoint(
                    request.getTracePointId(),
                    request.getFileName(),
                    className,
                    request.getLineNo(),
                    request.getClient(),
                    request.getFileHash(),
                    request.getConditionExpression(),
                    request.getExpireSecs(),
                    request.getExpireCount(),
                    request.isEnableTracing(),
                    request.isDisable(),
                    request.isPredefined());
            BrokerManager.publishApplicationStatus();
            if (request.getClient() != null) {
                BrokerManager.publishApplicationStatus(request.getClient());
            }
            return new PutTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient());
        } catch (Throwable error) {
            return new PutTracePointResponse().
                    setRequestId(request.getId()).
                    setClient(request.getClient()).
                    setError(error);
        }
    }

}
