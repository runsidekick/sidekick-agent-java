package com.runsidekick.agent.api.broker.publisher;

/**
 * @author serkan
 */
public interface EventPublisher {

    void publishEvent(String eventJson);

}
