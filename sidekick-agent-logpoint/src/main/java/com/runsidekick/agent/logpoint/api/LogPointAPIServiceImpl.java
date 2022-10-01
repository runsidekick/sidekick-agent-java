package com.runsidekick.agent.logpoint.api;

import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.api.broker.publisher.EventPublisher;
import com.runsidekick.agent.api.logpoint.LogPointAPIService;
import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.core.util.StringUtils;

import java.util.Set;
import java.util.UUID;

/**
 * @author yasin
 */
public class LogPointAPIServiceImpl implements LogPointAPIService {

    private static final String CLIENT;

    static {
        String client = BrokerManager.BROKER_CLIENT;
        if (StringUtils.isNullOrEmpty(client)) {
            client = BrokerManager.DEFAULT_CLIENT;
        }
        CLIENT = client;
    }

    @Override
    public EventPublisher getEventPublisher() {
        return BrokerManager.getEventPublisher();
    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        BrokerManager.setEventPublisher(eventPublisher);
    }

    @Override
    public String putLogPoint(String className, int lineNo, String client, String logExpression,
                              String fileHash, String conditionExpression,
                              int expireSecs, int expireCount, boolean stdoutEnabled, String logLevel,
                              boolean disable, boolean predefined, Set<String> tags) {
        if (StringUtils.isNullOrEmpty(client)) {
            client = CLIENT;
        }
        String id = UUID.randomUUID().toString();
        LogPointSupport.putLogPoint(
                id, className, lineNo,
                client, logExpression, fileHash, conditionExpression,
                expireSecs, expireCount, stdoutEnabled, logLevel, disable, predefined, tags);
        return id;
    }

    @Override
    public void updateLogPoint(String id, String client, String logExpression,
                               String conditionExpression, int expireSecs, int expireCount,
                               boolean disable, boolean stdoutEnabled, String logLevel,
                               boolean predefined, Set<String> tags) {
        if (StringUtils.isNullOrEmpty(client)) {
            client = CLIENT;
        }
        LogPointSupport.updateLogPoint(
                id, client, logExpression,
                conditionExpression, expireSecs, expireCount,
                disable, stdoutEnabled, logLevel, predefined, tags);
    }

    @Override
    public void removeLogPoint(String id, String client) {
        if (StringUtils.isNullOrEmpty(client)) {
            client = CLIENT;
        }
        LogPointSupport.removeLogPoint(id, client);
    }

    @Override
    public void enableLogPoint(String id, String client) {
        if (StringUtils.isNullOrEmpty(client)) {
            client = CLIENT;
        }
        LogPointSupport.enableLogPoint(id, client);
    }

    @Override
    public void disableLogPoint(String id, String client) {
        if (StringUtils.isNullOrEmpty(client)) {
            client = CLIENT;
        }
        LogPointSupport.disableLogPoint(id, client);
    }

}
