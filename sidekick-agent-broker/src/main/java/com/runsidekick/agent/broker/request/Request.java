package com.runsidekick.agent.broker.request;

/**
 * @author serkan
 */
public interface Request {

    default String getType() {
        return "Request";
    }

    String getName();

    String getId();

    String getClient();

}
