package com.runsidekick.agent.broker.handler.request.impl;

import com.runsidekick.agent.broker.request.Request;
import com.runsidekick.agent.broker.response.Response;
import com.runsidekick.agent.broker.handler.request.RequestHandler;
import org.slf4j.Logger;
import com.runsidekick.agent.core.logger.LoggerFactory;

/**
 * @author serkan
 */
public abstract class BaseRequestHandler<Req extends Request, Res extends Response>
        implements RequestHandler<Req, Res> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final String requestName;
    protected final Class<Req> requestClass;
    protected final Class<Res> responseClass;

    public BaseRequestHandler(String requestName, Class<Req> requestClass, Class<Res> responseClass) {
        this.requestName = requestName;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
    }

    @Override
    public String getRequestName() {
        return requestName;
    }

    @Override
    public Class<Req> getRequestClass() {
        return requestClass;
    }

    @Override
    public Class<Res> getResponseClass() {
        return responseClass;
    }

}
