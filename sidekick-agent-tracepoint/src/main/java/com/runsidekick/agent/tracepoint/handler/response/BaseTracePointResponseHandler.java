package com.runsidekick.agent.tracepoint.handler.response;

import com.runsidekick.agent.probe.util.ClassUtils;
import com.runsidekick.agent.tracepoint.error.TracePointErrorCodes;
import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.broker.handler.response.impl.BaseResponseHandler;
import com.runsidekick.agent.broker.response.Response;
import com.runsidekick.agent.core.util.StringUtils;

/**
 * @author serkan
 */
public abstract class BaseTracePointResponseHandler<Res extends Response>
        extends BaseResponseHandler<Res> {

    public BaseTracePointResponseHandler(String requestName,
                                         Class<Res> responseClass) {
        super(requestName, responseClass);
    }

    protected void validateLineNo(int lineNo) {
        if (lineNo <= 0) {
            logger.error("Line number is mandatory");
            throw new CodedException(TracePointErrorCodes.LINE_NUMBER_IS_MANDATORY);
        }
    }

    protected String validateAndGetClassName(String className, String fileName, int lineNo) {
        if (StringUtils.isNullOrEmpty(className) && StringUtils.isNullOrEmpty(fileName)) {
            logger.error("Class name or file name is mandatory");
            throw new CodedException(TracePointErrorCodes.CLASS_NAME_OR_FILE_NAME_IS_MANDATORY);
        }

        if (lineNo <= 0) {
            logger.error("Line number is mandatory");
            throw new CodedException(TracePointErrorCodes.LINE_NUMBER_IS_MANDATORY);
        }

        if (StringUtils.isNullOrEmpty(className)) {
            className = ClassUtils.extractClassName(fileName);
        }

        return className;
    }

}
