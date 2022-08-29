package com.runsidekick.agent.broker.handler.request;

import com.runsidekick.agent.broker.request.Request;
import com.runsidekick.agent.broker.response.Response;

/**
 * @author serkan
 */
public interface RequestHandler<Req extends Request, Res extends Response> {

    String getRequestName();
    Class<Req> getRequestClass();
    Class<Res> getResponseClass();

    Res handleRequest(Req request);

}
