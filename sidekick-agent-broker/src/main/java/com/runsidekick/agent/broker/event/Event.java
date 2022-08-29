package com.runsidekick.agent.broker.event;

/**
 * @author serkan
 */
public interface Event {

    default String getType() {
        return "Event";
    }

    String getName();

    String getId();

    boolean isSendAck();

    String getClient();

    long getTime();

    String getHostName();

    String getApplicationName();

    String getApplicationInstanceId();

}
