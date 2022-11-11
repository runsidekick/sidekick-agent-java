package com.runsidekick.agent.tracepoint.internal;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.ExecutorUtils;
import com.runsidekick.agent.core.util.StringUtils;
import com.runsidekick.agent.probe.ProbeSupport;
import com.runsidekick.agent.probe.condition.Condition;
import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.ProbeMetadata;
import com.runsidekick.agent.probe.domain.impl.ConditionAwareProbeAction;
import com.runsidekick.agent.probe.domain.impl.ExpiringProbeAction;
import com.runsidekick.agent.probe.domain.impl.RateLimitedProbeAction;
import com.runsidekick.agent.tracepoint.domain.TracePoint;
import com.runsidekick.agent.tracepoint.error.TracePointErrorCodes;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author serkan
 */
public final class TracePointManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TracePointManager.class);

    private static final Map<String, Probe> tracePointProbeMap = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> tagTracePointListMap = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService tracePointExpireScheduler =
            ExecutorUtils.newScheduledExecutorService("tracepoint-expire-scheduler");
    private static boolean initialized;

    private TracePointManager() {
    }

    //////////////////////////////////////////////////////////////////////////////////

    public synchronized static void ensureInitialized() {
        if (!initialized) {
            doInitialize();
            initialized = true;
        }
    }

    private static void doInitialize() {
        // TODO initialization logic if there is
        // Currently all initialization logic is
        // in the static initializer of this class.
        // So once this class is touched first time,
        // all the initialization logic is executed.
    }

    private static ProbeAction<TracePointContext> createTracePointAction(TracePointContext context) {
        return new ConditionAwareProbeAction<>(
                new RateLimitedProbeAction<>(
                    new ExpiringProbeAction<>(
                            new TracePointAction(context)
                    )
                )
        );
    }

    static ScheduledFuture scheduledExpireTask(TracePointContext context) {
        return tracePointExpireScheduler.schedule(
                new TracePointExpireTask(context), 1, TimeUnit.SECONDS);
    }

    static void expireTracePoint(TracePointContext context) {
        Probe probe = context.probe;

        LOGGER.debug(
                "Expiring tracepoint from class {} on line {} from client {}",
                probe.getClassName(), probe.getLineNo(), probe.getClient());

        if (context.removed) {
            return;
        }

        removeTracePoint(context.id, probe.getClient());

        BrokerManager.publishApplicationStatus();
        if (probe.getClient() != null) {
            BrokerManager.publishApplicationStatus(probe.getClient());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    public static List<TracePoint> listTracePoints() {
        return doListTracePoints(null);
    }

    public static List<TracePoint> listTracePoints(String client) {
        return doListTracePoints(client);
    }

    private static List<TracePoint> doListTracePoints(String client) {
        List<TracePoint> tracePoints = new ArrayList<>();
        for (Probe probe : tracePointProbeMap.values()) {
            if (client != null && !client.equals(probe.getClient())) {
                continue;
            }
            probe.actions().
                    stream().
                    filter(a -> TracePointAction.ACTION_ID.equals(a.id())).
                    forEach(a -> {
                        TracePointContext context = (TracePointContext) a.getContext();
                        if (context != null) {
                            TracePoint tracePoint =
                                    new TracePoint(
                                            context.id,
                                            probe.getFileName(),
                                            probe.getClassName(),
                                            probe.getLineNo(),
                                            probe.getClient(),
                                            context.conditionExpression,
                                            context.expireSecs,
                                            context.expireCount,
                                            context.enableTracing,
                                            context.disabled,
                                            context.tags);
                            tracePoints.add(tracePoint);
                        }
                    });
        }
        return tracePoints;
    }

    public static void putTracePoint(String id, String fileName, String className, int lineNo, String client,
                                     String fileHash, String conditionExpression, int expireSecs, int expireCount,
                                     boolean enableTracing, boolean disable, Set<String> tags) {
        LOGGER.debug(
                "Putting tracepoint with id {} to class {} on line {} from client {}",
                id, className, lineNo, client);

        Probe probe = null;
        try {
            ProbeMetadata metadata = ProbeSupport.getProbeMetadata(className, lineNo, client);

            if (StringUtils.hasValue(fileHash)) {
                String sourceCodeHash = ProbeSupport.getSourceCodeHash(
                        metadata.classLoader(), metadata.clazz(), metadata.classType(), className);
                if (StringUtils.hasValue(sourceCodeHash)) {
                    if (!sourceCodeHash.equals(fileHash)) {
                        LOGGER.error(
                                "Source code mismatch detected while putting tracepoint to class {} on line {} from client {}: {}",
                                className, lineNo, client);
                        throw new CodedException(
                                TracePointErrorCodes.SOURCE_CODE_MISMATCH_DETECTED, className, lineNo, client);
                    }
                }
            }

            Condition condition = null;
            if (StringUtils.hasValue(conditionExpression)) {
                condition = ProbeSupport.getCondition(
                        conditionExpression, className, metadata.classLoader(),
                        metadata.classType(), metadata.method(), lineNo);
            }

            probe = ProbeSupport.getOrPutProbe(fileName, className, lineNo, client);

            TracePointContext context =
                    new TracePointContext(
                            probe, id, conditionExpression,
                            expireSecs, expireCount, enableTracing,
                            condition, disable, tags);
            ProbeAction<TracePointContext> action = createTracePointAction(context);

            boolean added = ProbeSupport.addProbeAction(probe, action) == null;
            if (!added) {
                LOGGER.error(
                        "Tracepoint has been already added in class {} on line {} from client {} to put tracepoint",
                        className, lineNo, client);
                throw new CodedException(TracePointErrorCodes.TRACEPOINT_ALREADY_EXIST, className, lineNo, client);
            }

            tracePointProbeMap.put(id, probe);
            mapTracePointWithTags(id, context.tags);

            if (expireSecs > 0 && !context.hasTag()) {
                context.expireFuture =
                        tracePointExpireScheduler.schedule(
                                new TracePointExpireTask(context), expireSecs, TimeUnit.SECONDS);
            }
        } catch (Throwable t) {
            if (probe != null) {
                ProbeSupport.removeProbe(probe.getId(), true);
            }
            LOGGER.error(
                    "Error occurred while putting tracepoint to class {} on line {} from client {}: {}",
                    className, lineNo, client, t.getMessage());
            if (t instanceof CodedException) {
                throw (CodedException) t;
            } else {
                throw new CodedException(
                        TracePointErrorCodes.PUT_TRACEPOINT_FAILED, className, lineNo, client, t.getMessage());
            }
        }
    }

    public static synchronized void updateTracePoint(String id, String client,
                                                     String conditionExpression, int expireSecs, int expireCount,
                                                     boolean enableTracing, boolean disable, Set<String> tags) {
        LOGGER.debug(
                "Updating tracepoint with id {} from client {}",
                id, client);

        Probe probe = tracePointProbeMap.get(id);
        if (probe == null) {
            LOGGER.error(
                    "No tracepoint could be found with id {} from client {} to update tracepoint",
                    id, client);
            throw new CodedException(TracePointErrorCodes.NO_TRACEPOINT_EXIST_WITH_ID, id, client);
        }
        try {
            if (!probe.getClient().equals(client)) {
                LOGGER.error(
                        "Client {} has no access to tracepoint with id {}",
                        id, client);
                throw new CodedException(TracePointErrorCodes.CLIENT_HAS_NO_ACCESS_TO_TRACEPOINT, client, id);
            }

            LOGGER.debug(
                    "Updating tracepoint at class {} on line {} from client {}",
                    probe.getClassName(), probe.getLineNo(), client);

            ProbeMetadata metadata = ProbeSupport.getProbeMetadata(probe);

            Condition condition = null;
            if (StringUtils.hasValue(conditionExpression)) {
                condition = ProbeSupport.getCondition(
                        conditionExpression, probe.getClassName(), metadata.classLoader(),
                        metadata.classType(), metadata.method(), probe.getLineNo());
            }

            TracePointContext context =
                    new TracePointContext(
                            probe, id, conditionExpression,
                            expireSecs, expireCount, enableTracing,
                            condition, disable, tags);
            ProbeAction<TracePointContext> action = createTracePointAction(context);

            ProbeAction<TracePointContext> existingAction = ProbeSupport.replaceProbeAction(probe, action);
            if (existingAction == null) {
                LOGGER.error(
                        "No tracepoint could be found with id {} from client {} to remove tracepoint",
                        id, client);
                throw new CodedException(TracePointErrorCodes.NO_TRACEPOINT_EXIST_WITH_ID, id, client);
            }
            if (expireSecs > 0 && !context.hasTag()) {
                context.expireFuture =
                        tracePointExpireScheduler.schedule(
                                new TracePointExpireTask(context), expireSecs, TimeUnit.SECONDS);
            }

            TracePointContext existingContext = existingAction.getContext();
            if (existingContext != null) {
                existingContext.removed = true;
                existingContext.cancelExpireScheduleIfExist();
                unMapTracePointFromTags(id, existingContext.tags);
            }
            mapTracePointWithTags(id, context.tags);
        } catch (Throwable t) {
            LOGGER.error(
                    "Error occurred while updating tracepoint at class {} on line {} from client {}: {}",
                    probe.getClassName(), probe.getLineNo(), client, t.getMessage());
            if (t instanceof CodedException) {
                throw (CodedException) t;
            } else {
                throw new CodedException(
                        TracePointErrorCodes.UPDATE_TRACEPOINT_FAILED,
                        probe.getClassName(), probe.getLineNo(), client, t.getMessage());
            }
        }
    }

    public static synchronized void removeTracePoint(String id, String client) {
        LOGGER.debug(
                "Removing tracepoint from with id {} from client {}",
                id, client);

        Probe probe = tracePointProbeMap.get(id);
        if (probe == null) {
            LOGGER.error(
                    "No tracepoint could be found with id {} from client {} to remove tracepoint",
                    id, client);
            throw new CodedException(TracePointErrorCodes.NO_TRACEPOINT_EXIST_WITH_ID, id, client);
        }
        try {
            if (!probe.getClient().equals(client)) {
                LOGGER.error(
                        "Client {} has no access to tracepoint with id {}",
                        id, client);
                throw new CodedException(TracePointErrorCodes.CLIENT_HAS_NO_ACCESS_TO_TRACEPOINT, client, id);
            }

            LOGGER.debug(
                    "Removing tracepoint from class {} on line {} from client {}",
                    probe.getClassName(), probe.getLineNo(), client);

            ProbeAction<TracePointContext> existingAction =
                    ProbeSupport.removeProbeAction(probe, TracePointAction.ACTION_ID);
            if (existingAction == null) {
                LOGGER.error(
                        "No tracepoint could be found with id {} from client {} to remove tracepoint",
                        id, client);
                throw new CodedException(TracePointErrorCodes.NO_TRACEPOINT_EXIST_WITH_ID, id, client);
            }

            tracePointProbeMap.remove(id);
            unMapTracePointFromAllTags(id);

            ProbeSupport.removeProbe(probe.getId(), true);

            TracePointContext existingContext = existingAction.getContext();
            if (existingContext != null) {
                existingContext.removed = true;
                existingContext.cancelExpireScheduleIfExist();
            }
        } catch (Throwable t) {
            LOGGER.error(
                    "Error occurred while removing tracepoint from class {} on line {} from client {}: {}",
                    probe.getClassName(), probe.getLineNo(), client, t.getMessage());
            if (t instanceof CodedException) {
                throw (CodedException) t;
            } else {
                throw new CodedException(
                        TracePointErrorCodes.REMOVE_TRACEPOINT_FAILED,
                        probe.getClassName(), probe.getLineNo(), client, t.getMessage());
            }
        }
    }

    public static void enableTracePoint(String id, String client) {
        LOGGER.debug(
                "Enabling tracepoint with id {} from client {}",
                id, client);

        enableDisableTracePoint(id, client, false);
    }

    public static void disableTracePoint(String id, String client) {
        LOGGER.debug(
                "Disabling tracepoint with id {} from client {}",
                id, client);

        enableDisableTracePoint(id, client, true);
    }

    public static void enableTag(String tag, String client) {
        LOGGER.debug(
                "Enabling tracepoints with tag {} from client {}",
                tag, client);

        tagTracePointListMap.get(tag).forEach(tracePointId -> enableDisableTracePoint(tracePointId, client, false));
    }

    public static void disableTag(String tag, String client) {
        LOGGER.debug(
                "Disabling tracepoints with tag {} from client {}",
                tag, client);

        tagTracePointListMap.get(tag).forEach(tracePointId -> enableDisableTracePoint(tracePointId, client, true));
    }

    public static void removeAllTracePoints() {
        tracePointProbeMap.clear();
    }

    private static synchronized void enableDisableTracePoint(String id, String client, boolean disable) {
        Probe probe = tracePointProbeMap.get(id);
        if (probe == null) {
            LOGGER.error(
                    "No tracepoint could be found with id {} from client {} to {} tracepoint",
                    id, client, disable ? "disable" : "enable");
            throw new CodedException(TracePointErrorCodes.NO_TRACEPOINT_EXIST_WITH_ID, id, client);
        }
        try {
            if (!probe.getClient().equals(client)) {
                LOGGER.error(
                        "Client {} has no access to tracepoint with id {}",
                        id, client);
                throw new CodedException(TracePointErrorCodes.CLIENT_HAS_NO_ACCESS_TO_TRACEPOINT, client, id);
            }

            if (disable) {
                LOGGER.debug(
                        "Disabling tracepoint from class {} on line {} from client {}",
                        probe.getClassName(), probe.getLineNo(), client);
            } else {
                LOGGER.debug(
                        "Enabling tracepoint from class {} on line {} from client {}",
                        probe.getClassName(), probe.getLineNo(), client);
            }

            ProbeAction<TracePointContext> existingAction = probe.getAction(TracePointAction.ACTION_ID);
            if (existingAction == null) {
                LOGGER.error(
                        "No tracepoint could be found with id {} from client {} to {} tracepoint",
                        id, client, disable ? "disable" : "enable");
                throw new CodedException(TracePointErrorCodes.NO_TRACEPOINT_EXIST_WITH_ID, id, client);
            }

            TracePointContext existingContext = existingAction.getContext();
            if (existingContext != null) {
                existingContext.disabled = disable;
            }
        } catch (Throwable t) {
            LOGGER.error(
                    "Error occurred while {} tracepoint with id {} from client {}: {}",
                    disable ? "disabling" : "enabling", id, client, t.getMessage());
            throw new CodedException(
                    disable
                            ? TracePointErrorCodes.DISABLE_TRACEPOINT_WITH_ID_FAILED
                            : TracePointErrorCodes.ENABLE_TRACEPOINT_WITH_ID_FAILED,
                    id, client, t.getMessage());
        }
    }

    private static void mapTracePointWithTags(String id, Set<String> tags) {
        if (tags != null && tags.size() > 0) {
            for (String tag : tags) {
                if (!tagTracePointListMap.containsKey(tag)) {
                    tagTracePointListMap.putIfAbsent(tag, new ArrayList<>());
                }
                tagTracePointListMap.get(tag).add(id);
            }
        }
    }
    
    private static void unMapTracePointFromTags(String id, Set<String> tags) {
        if (tags != null && tags.size() > 0) {
            for (String tag : tags) {
                if (tagTracePointListMap.containsKey(tag)) {
                    tagTracePointListMap.get(tag).remove(id);
                }
            }
        }
    }
    
    private static void unMapTracePointFromAllTags(String id) {
        tagTracePointListMap.keySet().forEach(tag -> tagTracePointListMap.get(tag).remove(id));
    }
}
