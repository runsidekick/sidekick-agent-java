package com.runsidekick.agent.api.tracepoint;

import com.runsidekick.agent.api.broker.publisher.EventPublisher;

import java.util.concurrent.TimeUnit;

/**
 * @author serkan
 */
public final class TracePointAPI {

    public static final int TRACEPOINT_DEFAULT_EXPIRY_SECS = (int) TimeUnit.MINUTES.toSeconds(30); // 30 minutes
    public static final int TRACEPOINT_DEFAULT_EXPIRY_COUNT = 50;

    public static final int TRACEPOINT_MAX_EXPIRY_SECS = (int) TimeUnit.DAYS.toSeconds(1); // 1 day
    public static final int TRACEPOINT_MAX_EXPIRY_COUNT = 1000;

    private static TracePointAPIService tracePointAPIService = new NoOpTracePointAPIService();

    private TracePointAPI() {
    }

    public static TracePointAPIService getTracePointAPIService() {
        return tracePointAPIService;
    }

    public static void setTracePointAPIService(TracePointAPIService tracePointAPIService) {
        TracePointAPI.tracePointAPIService = tracePointAPIService;
    }

    private static class NoOpTracePointAPIService implements TracePointAPIService {

        @Override
        public EventPublisher getEventPublisher() {
            return null;
        }

        @Override
        public void setEventPublisher(EventPublisher eventPublisher) {
        }

        @Override
        public String putTracePoint(String className, int lineNo, String client,
                                    String fileHash, String conditionExpression,
                                    int expireSecs, int expireCount,
                                    boolean enableTracing, boolean disable) {
            return null;
        }

        @Override
        public void updateTracePoint(String id, String client,
                                     String conditionExpression, int expireSecs, int expireCount,
                                     boolean enableTracing, boolean disable) {
        }

        @Override
        public void removeTracePoint(String id, String client) {
        }

        @Override
        public void enableTracePoint(String id, String client) {
        }

        @Override
        public void disableTracePoint(String id, String client) {
        }

    }

    public static EventPublisher getEventPublisher() {
        return tracePointAPIService.getEventPublisher();
    }

    public static void setEventPublisher(EventPublisher eventPublisher) {
        tracePointAPIService.setEventPublisher(eventPublisher);
    }

    public static String putTracePoint(String className, int lineNo,
                                       String fileHash, String conditionExpression,
                                       int expireSecs, int expireCount,
                                       boolean enableTracing, boolean disable) {
        return tracePointAPIService.putTracePoint(
                className, lineNo, null,
                fileHash, conditionExpression,
                expireSecs, expireCount, enableTracing, disable);
    }

    public static void updateTracePoint(String id,
                                        String conditionExpression, int expireSecs, int expireCount,
                                        boolean enableTracing, boolean disable) {
        tracePointAPIService.updateTracePoint(
                id, null,
                conditionExpression, expireSecs,
                expireCount, enableTracing, disable);
    }

    public static void removeTracePoint(String id) {
        tracePointAPIService.removeTracePoint(id, null);
    }

    public static void enableTracePoint(String id) {
        tracePointAPIService.enableTracePoint(id, null);
    }

    public static void disableTracePoint(String id) {
        tracePointAPIService.disableTracePoint(id, null);
    }

}
