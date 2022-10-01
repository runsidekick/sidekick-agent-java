package com.runsidekick.agent.logpoint.handler.response;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.logpoint.domain.LogPoint;
import com.runsidekick.agent.logpoint.error.LogPointErrorCodes;
import com.runsidekick.agent.logpoint.response.FilterLogPointsResponse;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author yasin
 */
public class FilterLogPointsResponseHandler
        extends BaseLogPointResponseHandler<FilterLogPointsResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterLogPointsResponseHandler.class);

    public static final String RESPONSE_NAME = "FilterLogPointsResponse";

    public FilterLogPointsResponseHandler() {
        super(RESPONSE_NAME, FilterLogPointsResponse.class);
    }

    @Override
    public void handleResponse(FilterLogPointsResponse response) {
        List<LogPoint> logPointList = response.getLogPoints();
        logPointList.forEach(this::applyLogPoint);
    }

    private void applyLogPoint(LogPoint logPoint) {
        try {
            String className =
                    validateAndGetClassName(logPoint.getClassName(), logPoint.getFileName(), logPoint.getLineNo());

            LogPointSupport.putLogPoint(
                    logPoint.getId(),
                    logPoint.getFileName(),
                    className,
                    logPoint.getLineNo(),
                    logPoint.getClient(),
                    logPoint.getLogExpression(),
                    logPoint.getFileHash(),
                    logPoint.getConditionExpression(),
                    logPoint.getExpireSecs(),
                    logPoint.getExpireCount(),
                    logPoint.isStdoutEnabled(),
                    logPoint.getLogLevel(),
                    logPoint.isDisabled(),
                    logPoint.isPredefined(),
                    logPoint.getTags());
            BrokerManager.publishApplicationStatus();
            if (logPoint.getClient() != null) {
                BrokerManager.publishApplicationStatus(logPoint.getClient());
            }
        } catch (Throwable error) {
            boolean skipLogging = false;
            if (error instanceof CodedException) {
                skipLogging = ((CodedException) error).getCode() == LogPointErrorCodes.LOGPOINT_ALREADY_EXIST.getCode();
            }
            if (!skipLogging) {
                LOGGER.error("Unable to apply logpoint", error);
            }
        }
    }

}
