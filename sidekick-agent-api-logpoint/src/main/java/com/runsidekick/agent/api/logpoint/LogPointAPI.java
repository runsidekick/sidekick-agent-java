package com.runsidekick.agent.api.logpoint;

import com.runsidekick.agent.api.broker.publisher.EventPublisher;

import java.util.concurrent.TimeUnit;

/**
 * @author yasin
 */
public final class LogPointAPI {

    public static final int LOGPOINT_DEFAULT_EXPIRY_SECS = (int) TimeUnit.MINUTES.toSeconds(30); // 30 minutes
    public static final int LOGPOINT_DEFAULT_EXPIRY_COUNT = 50;

    public static final int LOGPOINT_MAX_EXPIRY_SECS = (int) TimeUnit.DAYS.toSeconds(1); // 1 day
    public static final int LOGPOINT_MAX_EXPIRY_COUNT = 1000;

    private static LogPointAPIService logPointAPIService = new NoOpLogPointAPIService();

    private LogPointAPI() {
    }

    public static LogPointAPIService getLogPointAPIService() {
        return logPointAPIService;
    }

    public static void setLogPointAPIService(LogPointAPIService logPointAPIService) {
        LogPointAPI.logPointAPIService = logPointAPIService;
    }

    private static class NoOpLogPointAPIService implements LogPointAPIService {

        @Override
        public EventPublisher getEventPublisher() {
            return null;
        }

        @Override
        public void setEventPublisher(EventPublisher eventPublisher) {
        }

        @Override
        public String putLogPoint(String className, int lineNo, String client, String logExpression,
                                  String fileHash, String conditionExpression,
                                  int expireSecs, int expireCount, boolean stdoutEnabled, String logLevel,
                                  boolean disable) {
            return null;
        }

        @Override
        public void updateLogPoint(String id, String client, String logExpression,
                                   String conditionExpression, int expireSecs, int expireCount, boolean disable,
                                   boolean stdoutEnabled, String logLevel) {
        }

        @Override
        public void removeLogPoint(String id, String client) {
        }

        @Override
        public void enableLogPoint(String id, String client) {
        }

        @Override
        public void disableLogPoint(String id, String client) {
        }

    }

    public static EventPublisher getEventPublisher() {
        return logPointAPIService.getEventPublisher();
    }

    public static void setEventPublisher(EventPublisher eventPublisher) {
        logPointAPIService.setEventPublisher(eventPublisher);
    }

    public static String putLogPoint(String className, int lineNo, String logExpression,
                                     String fileHash, String conditionExpression,
                                     int expireSecs, int expireCount,
                                     boolean stdoutEnabled, String logLevel, boolean disable) {
        return logPointAPIService.putLogPoint(
                className, lineNo, null, logExpression,
                fileHash, conditionExpression,
                expireSecs, expireCount, stdoutEnabled, logLevel, disable);
    }

    public static void updateLogPoint(String id, String logExpression,
                                      String conditionExpression, int expireSecs, int expireCount, boolean disable,
                                      boolean stdoutEnabled, String logLevel) {
        logPointAPIService.updateLogPoint(
                id, null, logExpression,
                conditionExpression, expireSecs,
                expireCount, disable, stdoutEnabled, logLevel);
    }

    public static void removeLogPoint(String id) {
        logPointAPIService.removeLogPoint(id, null);
    }

    public static void enableLogPoint(String id) {
        logPointAPIService.enableLogPoint(id, null);
    }

    public static void disableLogPoint(String id) {
        logPointAPIService.disableLogPoint(id, null);
    }

}
