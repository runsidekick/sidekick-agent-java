package com.runsidekick.agent.tracepoint.api;

import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.api.broker.publisher.EventPublisher;
import com.runsidekick.agent.api.tracepoint.TracePointAPIService;
import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.core.util.StringUtils;

import java.util.UUID;

/**
 * @author serkan
 */
public class TracePointAPIServiceImpl implements TracePointAPIService {

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
    public String putTracePoint(String className, int lineNo, String client,
                                String fileHash, String conditionExpression,
                                int expireSecs, int expireCount,
                                boolean enableTracing, boolean disable, boolean predefined) {
        if (StringUtils.isNullOrEmpty(client)) {
            client = CLIENT;
        }
        String id = UUID.randomUUID().toString();
        TracePointSupport.putTracePoint(
                id, className, lineNo, client,
                fileHash, conditionExpression,
                expireSecs, expireCount,
                enableTracing, disable, predefined);
        return id;
    }

    @Override
    public void updateTracePoint(String id, String client,
                                 String conditionExpression, int expireSecs, int expireCount,
                                 boolean enableTracing, boolean disable, boolean predefined) {
        if (StringUtils.isNullOrEmpty(client)) {
            client = CLIENT;
        }
        TracePointSupport.updateTracePoint(
                id, client,
                conditionExpression, expireSecs, expireCount,
                enableTracing, disable, predefined);
    }

    @Override
    public void removeTracePoint(String id, String client) {
        if (StringUtils.isNullOrEmpty(client)) {
            client = CLIENT;
        }
        TracePointSupport.removeTracePoint(id, client);
    }

    @Override
    public void enableTracePoint(String id, String client) {
        if (StringUtils.isNullOrEmpty(client)) {
            client = CLIENT;
        }
        TracePointSupport.enableTracePoint(id, client);
    }

    @Override
    public void disableTracePoint(String id, String client) {
        if (StringUtils.isNullOrEmpty(client)) {
            client = CLIENT;
        }
        TracePointSupport.disableTracePoint(id, client);
    }

}
