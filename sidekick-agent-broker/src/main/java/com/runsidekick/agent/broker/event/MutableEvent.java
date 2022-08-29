package com.runsidekick.agent.broker.event;

/**
 * @author serkan
 */
public interface MutableEvent extends Event {

    void setId(String id);

    void setClient(String client);

    void setTime(long time);

    void setHostName(String hostName);

    void setApplicationName(String applicationName);

    void setApplicationInstanceId(String applicationInstanceId);

}
