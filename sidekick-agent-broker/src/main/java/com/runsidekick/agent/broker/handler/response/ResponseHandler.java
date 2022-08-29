package com.runsidekick.agent.broker.handler.response;

import com.runsidekick.agent.broker.response.Response;

/**
 * @author serkan
 */
public interface ResponseHandler<Res extends Response> {

    String getResponseName();

    Class<Res> getResponseClass();

    void handleResponse(Res response);

}
