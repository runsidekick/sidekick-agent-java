package com.runsidekick.agent.tracepoint.handler.response;

import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.tracepoint.domain.TracePoint;
import com.runsidekick.agent.tracepoint.error.TracePointErrorCodes;
import com.runsidekick.agent.tracepoint.response.FilterTracePointsResponse;
import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.error.CodedException;
import org.slf4j.Logger;
import com.runsidekick.agent.core.logger.LoggerFactory;

import java.util.List;

/**
 * @author serkan
 */
public class FilterTracePointsResponseHandler
        extends BaseTracePointResponseHandler<FilterTracePointsResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterTracePointsResponseHandler.class);

    public static final String RESPONSE_NAME = "FilterTracePointsResponse";

    public FilterTracePointsResponseHandler() {
        super(RESPONSE_NAME, FilterTracePointsResponse.class);
    }

    @Override
    public void handleResponse(FilterTracePointsResponse response) {
        List<TracePoint> tracePointList = response.getTracePoints();
        tracePointList.forEach(this::applyTracePoint);
    }

    private void applyTracePoint(TracePoint tracePoint) {
        try {
            String className =
                    validateAndGetClassName(tracePoint.getClassName(), tracePoint.getFileName(), tracePoint.getLineNo());

            TracePointSupport.putTracePoint(
                    tracePoint.getId(),
                    tracePoint.getFileName(),
                    className,
                    tracePoint.getLineNo(),
                    tracePoint.getClient(),
                    tracePoint.getFileHash(),
                    tracePoint.getConditionExpression(),
                    tracePoint.getExpireSecs(),
                    tracePoint.getExpireCount(),
                    tracePoint.isTracingEnabled(),
                    tracePoint.isDisabled(),
                    tracePoint.getTags());
            BrokerManager.publishApplicationStatus();
            if (tracePoint.getClient() != null) {
                BrokerManager.publishApplicationStatus(tracePoint.getClient());
            }
        } catch (Throwable error) {
            boolean skipLogging = false;
            if (error instanceof CodedException) {
                skipLogging = ((CodedException) error).getCode() == TracePointErrorCodes.TRACEPOINT_ALREADY_EXIST.getCode();
            }
            if (!skipLogging) {
                LOGGER.error("Unable to apply tracepoint", error);
            }
        }
    }

}
