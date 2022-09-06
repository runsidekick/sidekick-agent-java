package com.runsidekick.agent.api.logpoint;

import com.runsidekick.agent.api.broker.publisher.EventPublisher;

/**
 * @author yasin
 */
public interface LogPointAPIService {

    EventPublisher getEventPublisher();

    void setEventPublisher(EventPublisher eventPublisher);

    String putLogPoint(String className, int lineNo, String client, String logExpression,
                       String fileHash, String conditionExpression, int expireSecs, int expireCount,
                       boolean stdoutEnabled, String logLevel, boolean disable, boolean predefined);

    void updateLogPoint(String id, String client, String logExpression,
                        String conditionExpression, int expireSecs, int expireCount, boolean disable,
                        boolean stdoutEnabled, String logLevel, boolean predefined);

    void removeLogPoint(String id, String client);

    void enableLogPoint(String id, String client);

    void disableLogPoint(String id, String client);

}
