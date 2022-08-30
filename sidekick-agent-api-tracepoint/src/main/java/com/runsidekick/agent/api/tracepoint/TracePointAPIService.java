package com.runsidekick.agent.api.tracepoint;

import com.runsidekick.agent.api.broker.publisher.EventPublisher;

/**
 * @author serkan
 */
public interface TracePointAPIService {

    EventPublisher getEventPublisher();
    void setEventPublisher(EventPublisher eventPublisher);

    String putTracePoint(String className, int lineNo, String client,
                         String fileHash, String conditionExpression, int expireSecs, int expireCount,
                         boolean enableTracing);
    void updateTracePoint(String id, String client,
                          String conditionExpression, int expireSecs, int expireCount,
                          boolean enableTracing, boolean disable);
    void removeTracePoint(String id, String client);
    void enableTracePoint(String id, String client);
    void disableTracePoint(String id, String client);

}
