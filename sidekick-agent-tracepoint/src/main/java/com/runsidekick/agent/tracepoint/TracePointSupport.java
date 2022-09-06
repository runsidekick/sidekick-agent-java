package com.runsidekick.agent.tracepoint;

import com.runsidekick.agent.tracepoint.api.TracePointAPIServiceImpl;
import com.runsidekick.agent.tracepoint.domain.TracePoint;
import com.runsidekick.agent.tracepoint.internal.TracePointManager;
import com.runsidekick.agent.api.tracepoint.TracePointAPI;
import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.event.Event;
import com.runsidekick.agent.core.logger.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author serkan
 */
public final class TracePointSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(TracePointSupport.class);

    public static final int TRACEPOINT_DEFAULT_EXPIRY_SECS = TracePointAPI.TRACEPOINT_DEFAULT_EXPIRY_SECS;
    public static final int TRACEPOINT_DEFAULT_EXPIRY_COUNT = TracePointAPI.TRACEPOINT_DEFAULT_EXPIRY_COUNT;

    public static final int TRACEPOINT_MAX_EXPIRY_SECS = TracePointAPI.TRACEPOINT_MAX_EXPIRY_SECS;
    public static final int TRACEPOINT_MAX_EXPIRY_COUNT = TracePointAPI.TRACEPOINT_MAX_EXPIRY_COUNT;

    private static boolean initialized;

    private TracePointSupport() {
    }

    //////////////////////////////////////////////////////////////////////////////////

    public synchronized static void ensureInitialized() {
        if (!initialized) {
            doInitialize();
            initialized = true;
        }
    }

    private static void doInitialize() {
        TracePointManager.ensureInitialized();
        TracePointAPI.setTracePointAPIService(new TracePointAPIServiceImpl());
    }

    public static List<TracePoint> listTracePoints() {
        return TracePointManager.listTracePoints();
    }

    public static List<String> listTracePointIds() {
        return TracePointManager.listTracePoints().stream().map(TracePoint::getId).collect(Collectors.toList());
    }

    public static List<TracePoint> listTracePoints(String client) {
        return TracePointManager.listTracePoints(client);
    }

    public static List<String> listTracePointIds(String client) {
        return TracePointManager.listTracePoints(client).stream().map(TracePoint::getId).collect(Collectors.toList());
    }

    public static void putTracePoint(String id, String className, int lineNo, String client,
                                     String fileHash, String conditionExpression, int expireSecs, int expireCount,
                                     boolean enableTracing, boolean disable, boolean predefined) {
        putTracePoint(id, null, className, lineNo, client, fileHash,
                conditionExpression, expireSecs, expireCount, enableTracing, disable, predefined);
    }

    public static void putTracePoint(String id, String fileName, String className, int lineNo, String client,
                                     String fileHash, String conditionExpression, int expireSecs, int expireCount,
                                     boolean enableTracing, boolean disable, boolean predefined) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        if (expireSecs > 0) {
            expireSecs = Math.min(expireSecs, TRACEPOINT_MAX_EXPIRY_SECS);
        } else {
            expireSecs = TRACEPOINT_DEFAULT_EXPIRY_SECS;
        }
        if (expireCount > 0) {
            expireCount = Math.min(expireCount, TRACEPOINT_MAX_EXPIRY_COUNT);
        } else {
            expireCount = TRACEPOINT_DEFAULT_EXPIRY_COUNT;
        }
        TracePointManager.putTracePoint(
                id, fileName, className, lineNo, client, fileHash, conditionExpression,
                expireSecs, expireCount, enableTracing, disable, predefined);
    }

    public static void updateTracePoint(String id, String client,
                                        String conditionExpression, int expireSecs, int expireCount,
                                        boolean enableTracing, boolean disable, boolean predefined) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        if (expireSecs > 0) {
            expireSecs = Math.min(expireSecs, TRACEPOINT_MAX_EXPIRY_SECS);
        } else {
            expireSecs = TRACEPOINT_DEFAULT_EXPIRY_SECS;
        }
        if (expireCount > 0) {
            expireCount = Math.min(expireCount, TRACEPOINT_MAX_EXPIRY_COUNT);
        } else {
            expireCount = TRACEPOINT_DEFAULT_EXPIRY_COUNT;
        }
        TracePointManager.updateTracePoint(
                id, client, conditionExpression,
                expireSecs, expireCount, enableTracing, disable, predefined);
    }

    public static void removeTracePoint(String id, String client) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        TracePointManager.removeTracePoint(id, client);
    }

    public static void enableTracePoint(String id, String client) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        TracePointManager.enableTracePoint(id, client);
    }

    public static void disableTracePoint(String id, String client) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        TracePointManager.disableTracePoint(id, client);
    }

    public static void publishTracePointEvent(Event event) {
        BrokerManager.serializeAndPublishEvent(event);
    }

    public static void publishTracePointEvent(Supplier<Event> eventSupplier) {
        BrokerManager.serializeAndPublishEvent(eventSupplier);
    }

}
