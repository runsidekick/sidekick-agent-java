package com.runsidekick.agent.logpoint.handler.response;

import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.broker.handler.response.impl.BaseResponseHandler;
import com.runsidekick.agent.broker.response.Response;
import com.runsidekick.agent.core.util.StringUtils;
import com.runsidekick.agent.logpoint.error.LogPointErrorCodes;
import com.runsidekick.agent.probe.util.ClassUtils;

/**
 * @author yasin
 */
public abstract class BaseLogPointResponseHandler<Res extends Response>
        extends BaseResponseHandler<Res> {

    public BaseLogPointResponseHandler(String responseName,
                                       Class<Res> responseClass) {
        super(responseName, responseClass);
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
