package com.runsidekick.agent.logpoint.handler.request;

import com.runsidekick.agent.logpoint.error.LogPointErrorCodes;
import com.runsidekick.agent.probe.util.ClassUtils;
import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.broker.handler.request.impl.BaseRequestHandler;
import com.runsidekick.agent.broker.request.Request;
import com.runsidekick.agent.broker.response.Response;
import com.runsidekick.agent.core.util.StringUtils;

/**
 * @author yasin
 */
public abstract class BaseLogPointRequestHandler<Req extends Request, Res extends Response>
        extends BaseRequestHandler<Req, Res> {

    public BaseLogPointRequestHandler(String requestName,
                                        Class<Req> requestClass,
                                        Class<Res> responseClass) {
        super(requestName, requestClass, responseClass);
    }

    protected void validateLineNo(int lineNo) {
        if (lineNo <= 0) {
            logger.error("Line number is mandatory");
            throw new CodedException(LogPointErrorCodes.LINE_NUMBER_IS_MANDATORY);
        }
    }

    protected String validateAndGetClassName(String className, String fileName, int lineNo) {
        if (StringUtils.isNullOrEmpty(className) && StringUtils.isNullOrEmpty(fileName)) {
            logger.error("Class name or file name is mandatory");
            throw new CodedException(LogPointErrorCodes.CLASS_NAME_OR_FILE_NAME_IS_MANDATORY);
        }

        if (lineNo <= 0) {
            logger.error("Line number is mandatory");
            throw new CodedException(LogPointErrorCodes.LINE_NUMBER_IS_MANDATORY);
        }

        if (StringUtils.isNullOrEmpty(className)) {
            className = ClassUtils.extractClassName(fileName);
        }

        return className;
    }

}
