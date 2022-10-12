package com.runsidekick.agent.logpoint;

import com.runsidekick.agent.api.logpoint.LogPointAPI;
import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.event.Event;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.logpoint.api.LogPointAPIServiceImpl;
import com.runsidekick.agent.logpoint.domain.LogPoint;
import com.runsidekick.agent.logpoint.event.LogPointEvent;
import com.runsidekick.agent.logpoint.internal.LogPointManager;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yasin
 */
public final class LogPointSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogPointSupport.class);

    public static final int LOGPOINT_DEFAULT_EXPIRY_SECS = LogPointAPI.LOGPOINT_DEFAULT_EXPIRY_SECS;
    public static final int LOGPOINT_DEFAULT_EXPIRY_COUNT = LogPointAPI.LOGPOINT_DEFAULT_EXPIRY_COUNT;

    public static final int LOGPOINT_MAX_EXPIRY_SECS = LogPointAPI.LOGPOINT_MAX_EXPIRY_SECS;
    public static final int LOGPOINT_MAX_EXPIRY_COUNT = LogPointAPI.LOGPOINT_MAX_EXPIRY_COUNT;

    private static boolean initialized;

    private LogPointSupport() {
    }

    //////////////////////////////////////////////////////////////////////////////////

    public synchronized static void ensureInitialized() {
        if (!initialized) {
            doInitialize();
            initialized = true;
        }
    }

    private static void doInitialize() {
        LogPointManager.ensureInitialized();
        LogPointAPI.setLogPointAPIService(new LogPointAPIServiceImpl());
    }

    public static List<LogPoint> listLogPoints() {
        return LogPointManager.listLogPoints();
    }

    public static List<String> listLogPointIds() {
        return LogPointManager.listLogPoints().stream().map(LogPoint::getId).collect(Collectors.toList());
    }

    public static List<LogPoint> listLogPoints(String client) {
        return LogPointManager.listLogPoints(client);
    }

    public static List<String> listLogPointIds(String client) {
        return LogPointManager.listLogPoints(client).stream().map(LogPoint::getId).collect(Collectors.toList());
    }

    public static void putLogPoint(String id, String className, int lineNo, String client, String logExpression,
                                   String fileHash, String conditionExpression, int expireSecs, int expireCount,
                                   boolean stdoutEnabled, String logLevel, boolean disable, Set<String> tags) {
        putLogPoint(id, null, className, lineNo, client, logExpression, fileHash,
                conditionExpression, expireSecs, expireCount, stdoutEnabled, logLevel, disable, tags);
    }

    public static void putLogPoint(String id, String fileName, String className, int lineNo, String client,
                                   String logExpression, String fileHash, String conditionExpression,
                                   int expireSecs, int expireCount, boolean stdoutEnabled, String logLevel,
                                   boolean disable, Set<String> tags) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        if (expireSecs > 0) {
            expireSecs = Math.min(expireSecs, LOGPOINT_MAX_EXPIRY_SECS);
        } else {
            expireSecs = LOGPOINT_DEFAULT_EXPIRY_SECS;
        }
        if (expireCount > 0) {
            expireCount = Math.min(expireCount, LOGPOINT_MAX_EXPIRY_COUNT);
        } else {
            expireCount = LOGPOINT_DEFAULT_EXPIRY_COUNT;
        }
        LogPointManager.putLogPoint(
                id, fileName, className, lineNo, client, logExpression, fileHash, conditionExpression,
                expireSecs, expireCount, stdoutEnabled, logLevel, disable, tags);
    }

    public static void updateLogPoint(String id, String client, String logExpression,
                                      String conditionExpression, int expireSecs, int expireCount, boolean disable,
                                      boolean stdoutEnabled, String logLevel, Set<String> tags) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        if (expireSecs > 0) {
            expireSecs = Math.min(expireSecs, LOGPOINT_MAX_EXPIRY_SECS);
        } else {
            expireSecs = LOGPOINT_DEFAULT_EXPIRY_SECS;
        }
        if (expireCount > 0) {
            expireCount = Math.min(expireCount, LOGPOINT_MAX_EXPIRY_COUNT);
        } else {
            expireCount = LOGPOINT_DEFAULT_EXPIRY_COUNT;
        }
        LogPointManager.updateLogPoint(
                id, client, logExpression, conditionExpression,
                expireSecs, expireCount, disable, stdoutEnabled, logLevel, tags);
    }

    public static void removeLogPoint(String id, String client) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        LogPointManager.removeLogPoint(id, client);
    }

    public static void enableLogPoint(String id, String client) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        LogPointManager.enableLogPoint(id, client);
    }

    public static void disableLogPoint(String id, String client) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        LogPointManager.disableLogPoint(id, client);
    }

    public static void enableTag(String tag, String client) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        LogPointManager.enableTag(tag, client);
    }

    public static void disableTag(String tag, String client) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        LogPointManager.disableTag(tag, client);
    }

    public static void publishLogPointEvent(Event event) {
        BrokerManager.serializeAndPublishEvent(event);
    }

    public static void printLogMessage(String logLevel, LogPointEvent logPointEvent) {
        System.out.printf("%-24s[%-5s] %s\n", logPointEvent.getCreatedAt(), logLevel, logPointEvent.getLogMessage());
    }
}
