package com.runsidekick.agent.broker.handler.response.impl;

import com.runsidekick.agent.broker.handler.response.ResponseHandler;
import com.runsidekick.agent.broker.response.Response;
import org.slf4j.Logger;
import com.runsidekick.agent.core.logger.LoggerFactory;

/**
 * @author serkan
 */
public abstract class BaseResponseHandler<Res extends Response>
        implements ResponseHandler<Res> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final String responseName;
    protected final Class<Res> responseClass;

    public BaseResponseHandler(String responseName, Class<Res> responseClass) {
        this.responseName = responseName;
        this.responseClass = responseClass;
    }

    @Override
    public String getResponseName() {
        return responseName;
    }


    @Override
    public Class<Res> getResponseClass() {
        return responseClass;
    }

}
