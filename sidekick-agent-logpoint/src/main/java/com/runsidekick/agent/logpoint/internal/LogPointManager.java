package com.runsidekick.agent.logpoint.internal;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.ExecutorUtils;
import com.runsidekick.agent.core.util.StringUtils;
import com.runsidekick.agent.logpoint.expression.execute.LogPointExpressionExecutor;
import com.runsidekick.agent.logpoint.expression.execute.impl.MustacheExpressionExecutor;
import com.runsidekick.agent.probe.ProbeSupport;
import com.runsidekick.agent.probe.condition.Condition;
import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.ProbeMetadata;
import com.runsidekick.agent.probe.domain.impl.ConditionAwareProbeAction;
import com.runsidekick.agent.probe.domain.impl.ExpiringProbeAction;
import com.runsidekick.agent.probe.domain.impl.RateLimitedProbeAction;
import com.runsidekick.agent.logpoint.domain.LogPoint;
import com.runsidekick.agent.logpoint.error.LogPointErrorCodes;
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
 * @author yasin
 */
public final class LogPointManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogPointManager.class);

    private static final Map<String, Probe> logPointProbeMap = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> tagLogPointListMap = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService logPointExpireScheduler =
            ExecutorUtils.newScheduledExecutorService("logpoint-expire-scheduler");
    private static final LogPointExpressionExecutor expressionExecutor = new MustacheExpressionExecutor();
    private static boolean initialized;

    private LogPointManager() {
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

    private static ProbeAction<LogPointContext> createLogPointAction(LogPointContext context) {
        return new ConditionAwareProbeAction<>(
                new RateLimitedProbeAction<>(
                        new ExpiringProbeAction<>(
                                new LogPointAction(context, expressionExecutor)
                        )
                )
        );
    }

    static ScheduledFuture scheduledExpireTask(LogPointContext context) {
        return logPointExpireScheduler.schedule(
                new LogPointExpireTask(context), 1, TimeUnit.SECONDS);
    }

    static void expireLogPoint(LogPointContext context) {
        Probe probe = context.probe;

        LOGGER.debug(
                "Expiring logpoint from class {} on line {} from client {}",
                probe.getClassName(), probe.getLineNo(), probe.getClient());

        if (context.removed) {
            return;
        }

        removeLogPoint(context.id, probe.getClient());

        BrokerManager.publishApplicationStatus();
        if (probe.getClient() != null) {
            BrokerManager.publishApplicationStatus(probe.getClient());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    public static List<LogPoint> listLogPoints() {
        return doListLogPoints(null);
    }

    public static List<LogPoint> listLogPoints(String client) {
        return doListLogPoints(client);
    }

    private static List<LogPoint> doListLogPoints(String client) {
        List<LogPoint> logPoints = new ArrayList<>();
        for (Probe probe : logPointProbeMap.values()) {
            if (client != null && !client.equals(probe.getClient())) {
                continue;
            }
            probe.actions().
                    stream().
                    filter(a -> LogPointAction.ACTION_ID.equals(a.id())).
                    forEach(a -> {
                        LogPointContext context = (LogPointContext) a.getContext();
                        if (context != null) {
                            LogPoint logPoint =
                                    new LogPoint(
                                            context.id,
                                            probe.getFileName(),
                                            probe.getClassName(),
                                            probe.getLineNo(),
                                            probe.getClient(),
                                            context.logExpression,
                                            context.conditionExpression,
                                            context.expireSecs,
                                            context.expireCount,
                                            context.disabled,
                                            context.stdoutEnabled,
                                            context.logLevel,
                                            context.predefined,
                                            context.tags);
                            logPoints.add(logPoint);
                        }
                    });
        }
        return logPoints;
    }

    public static void putLogPoint(String id, String fileName, String className, int lineNo, String client,
                                   String logExpression, String fileHash, String conditionExpression,
                                   int expireSecs, int expireCount, boolean stdoutEnabled, String logLevel,
                                   boolean disable, boolean predefined, Set<String> tags) {
        LOGGER.debug(
                "Putting logpoint with id {} to class {} on line {} from client {}",
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
                                "Source code mismatch detected while putting logpoint to class {} on line {} from client {}: {}",
                                className, lineNo, client);
                        throw new CodedException(
                                LogPointErrorCodes.SOURCE_CODE_MISMATCH_DETECTED, className, lineNo, client);
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

            LogPointContext context =
                    new LogPointContext(probe, id, logExpression, conditionExpression,
                            expireSecs, expireCount, condition, disable, stdoutEnabled, logLevel, predefined, tags);
            ProbeAction<LogPointContext> action = createLogPointAction(context);

            boolean added = ProbeSupport.addProbeAction(probe, action) == null;
            if (!added) {
                LOGGER.error(
                        "Logpoint has been already added in class {} on line {} from client {} to put logpoint",
                        className, lineNo, client);
                throw new CodedException(LogPointErrorCodes.LOGPOINT_ALREADY_EXIST, className, lineNo, client);
            }

            logPointProbeMap.put(id, probe);
            mapLogPointWithTags(id, context.tags);

            if (expireSecs > 0 && !predefined) {
                context.expireFuture =
                        logPointExpireScheduler.schedule(
                                new LogPointExpireTask(context), expireSecs, TimeUnit.SECONDS);
            }
        } catch (Throwable t) {
            if (probe != null) {
                ProbeSupport.removeProbe(probe.getId(), true);
            }
            LOGGER.error(
                    "Error occurred while putting logpoint to class {} on line {} from client {}: {}",
                    className, lineNo, client, t.getMessage());
            if (t instanceof CodedException) {
                throw (CodedException) t;
            } else {
                throw new CodedException(
                        LogPointErrorCodes.PUT_LOGPOINT_FAILED, className, lineNo, client, t.getMessage());
            }
        }
    }

    public static synchronized void updateLogPoint(String id, String client, String logExpression,
                                                   String conditionExpression, int expireSecs, int expireCount,
                                                   boolean disable, boolean stdoutEnabled, String logLevel,
                                                   boolean predefined, Set<String> tags) {
        LOGGER.debug(
                "Updating logpoint with id {} from client {}",
                id, client);

        Probe probe = logPointProbeMap.get(id);
        if (probe == null) {
            LOGGER.error(
                    "No logpoint could be found with id {} from client {} to update logpoint",
                    id, client);
            throw new CodedException(LogPointErrorCodes.NO_LOGPOINT_EXIST_WITH_ID, id, client);
        }
        try {
            if (!probe.getClient().equals(client)) {
                LOGGER.error(
                        "Client {} has no access to logpoint with id {}",
                        id, client);
                throw new CodedException(LogPointErrorCodes.CLIENT_HAS_NO_ACCESS_TO_LOGPOINT, client, id);
            }

            LOGGER.debug(
                    "Updating logpoint at class {} on line {} from client {}",
                    probe.getClassName(), probe.getLineNo(), client);

            ProbeMetadata metadata = ProbeSupport.getProbeMetadata(probe);

            Condition condition = null;
            if (StringUtils.hasValue(conditionExpression)) {
                condition = ProbeSupport.getCondition(
                        conditionExpression, probe.getClassName(), metadata.classLoader(),
                        metadata.classType(), metadata.method(), probe.getLineNo());
            }

            LogPointContext context =
                    new LogPointContext(
                            probe, id, logExpression, conditionExpression,
                            expireSecs, expireCount, condition, disable, stdoutEnabled, logLevel, predefined, tags);
            ProbeAction<LogPointContext> action = createLogPointAction(context);

            ProbeAction<LogPointContext> existingAction = ProbeSupport.replaceProbeAction(probe, action);
            if (existingAction == null) {
                LOGGER.error(
                        "No logpoint could be found with id {} from client {} to remove logpoint",
                        id, client);
                throw new CodedException(LogPointErrorCodes.NO_LOGPOINT_EXIST_WITH_ID, id, client);
            }
            if (expireSecs > 0 && !predefined) {
                context.expireFuture =
                        logPointExpireScheduler.schedule(
                                new LogPointExpireTask(context), expireSecs, TimeUnit.SECONDS);
            }

            LogPointContext existingContext = existingAction.getContext();
            if (existingContext != null) {
                existingContext.removed = true;
                existingContext.cancelExpireScheduleIfExist();
                unMapLogPointFromTags(id, existingContext.tags);
            }
            mapLogPointWithTags(id, context.tags);
        } catch (Throwable t) {
            LOGGER.error(
                    "Error occurred while updating logpoint at class {} on line {} from client {}: {}",
                    probe.getClassName(), probe.getLineNo(), client, t.getMessage());
            if (t instanceof CodedException) {
                throw (CodedException) t;
            } else {
                throw new CodedException(
                        LogPointErrorCodes.UPDATE_LOGPOINT_FAILED,
                        probe.getClassName(), probe.getLineNo(), client, t.getMessage());
            }
        }
    }

    public static synchronized void removeLogPoint(String id, String client) {
        LOGGER.debug(
                "Removing logpoint from with id {} from client {}",
                id, client);

        Probe probe = logPointProbeMap.get(id);
        if (probe == null) {
            LOGGER.error(
                    "No logpoint could be found with id {} from client {} to remove logpoint",
                    id, client);
            throw new CodedException(LogPointErrorCodes.NO_LOGPOINT_EXIST_WITH_ID, id, client);
        }
        try {
            if (!probe.getClient().equals(client)) {
                LOGGER.error(
                        "Client {} has no access to logpoint with id {}",
                        id, client);
                throw new CodedException(LogPointErrorCodes.CLIENT_HAS_NO_ACCESS_TO_LOGPOINT, client, id);
            }

            LOGGER.debug(
                    "Removing logpoint from class {} on line {} from client {}",
                    probe.getClassName(), probe.getLineNo(), client);

            ProbeAction<LogPointContext> existingAction =
                    ProbeSupport.removeProbeAction(probe, LogPointAction.ACTION_ID);
            if (existingAction == null) {
                LOGGER.error(
                        "No logpoint could be found with id {} from client {} to remove logpoint",
                        id, client);
                throw new CodedException(LogPointErrorCodes.NO_LOGPOINT_EXIST_WITH_ID, id, client);
            }

            logPointProbeMap.remove(id);
            unMapLogPointFromAllTags(id);

            ProbeSupport.removeProbe(probe.getId(), true);

            LogPointContext existingContext = existingAction.getContext();
            if (existingContext != null) {
                existingContext.removed = true;
                existingContext.cancelExpireScheduleIfExist();
            }
        } catch (Throwable t) {
            LOGGER.error(
                    "Error occurred while removing logpoint from class {} on line {} from client {}: {}",
                    probe.getClassName(), probe.getLineNo(), client, t.getMessage());
            if (t instanceof CodedException) {
                throw (CodedException) t;
            } else {
                throw new CodedException(
                        LogPointErrorCodes.REMOVE_LOGPOINT_FAILED,
                        probe.getClassName(), probe.getLineNo(), client, t.getMessage());
            }
        }
    }

    public static void enableLogPoint(String id, String client) {
        LOGGER.debug(
                "Enabling logpoint with id {} from client {}",
                id, client);

        enableDisableLogPoint(id, client, false);
    }

    public static void disableLogPoint(String id, String client) {
        LOGGER.debug(
                "Disabling logpoint with id {} from client {}",
                id, client);

        enableDisableLogPoint(id, client, true);
    }

    private static synchronized void enableDisableLogPoint(String id, String client, boolean disable) {
        Probe probe = logPointProbeMap.get(id);
        if (probe == null) {
            LOGGER.error(
                    "No logpoint could be found with id {} from client {} to {} logpoint",
                    id, client, disable ? "disable" : "enable");
            throw new CodedException(LogPointErrorCodes.NO_LOGPOINT_EXIST_WITH_ID, id, client);
        }
        try {
            if (!probe.getClient().equals(client)) {
                LOGGER.error(
                        "Client {} has no access to logpoint with id {}",
                        id, client);
                throw new CodedException(LogPointErrorCodes.CLIENT_HAS_NO_ACCESS_TO_LOGPOINT, client, id);
            }

            if (disable) {
                LOGGER.debug(
                        "Disabling logpoint from class {} on line {} from client {}",
                        probe.getClassName(), probe.getLineNo(), client);
            } else {
                LOGGER.debug(
                        "Enabling logpoint from class {} on line {} from client {}",
                        probe.getClassName(), probe.getLineNo(), client);
            }

            ProbeAction<LogPointContext> existingAction = probe.getAction(LogPointAction.ACTION_ID);
            if (existingAction == null) {
                LOGGER.error(
                        "No logpoint could be found with id {} from client {} to {} logpoint",
                        id, client, disable ? "disable" : "enable");
                throw new CodedException(LogPointErrorCodes.NO_LOGPOINT_EXIST_WITH_ID, id, client);
            }

            LogPointContext existingContext = existingAction.getContext();
            if (existingContext != null) {
                existingContext.disabled = disable;
            }
        } catch (Throwable t) {
            LOGGER.error(
                    "Error occurred while {} logpoint with id {} from client {}: {}",
                    disable ? "disabling" : "enabling", id, client, t.getMessage());
            throw new CodedException(
                    disable
                            ? LogPointErrorCodes.DISABLE_LOGPOINT_WITH_ID_FAILED
                            : LogPointErrorCodes.ENABLE_LOGPOINT_WITH_ID_FAILED,
                    id, client, t.getMessage());
        }
    }

    private static void mapLogPointWithTags(String id, Set<String> tags) {
        if (tags != null && tags.size() > 0) {
            for (String tag : tags) {
                if (!tagLogPointListMap.containsKey(tag)) {
                    tagLogPointListMap.putIfAbsent(tag, new ArrayList<>());
                }
                tagLogPointListMap.get(tag).add(id);
            }
        }
    }

    private static void unMapLogPointFromTags(String id, Set<String> tags) {
        if (tags != null && tags.size() > 0) {
            for (String tag : tags) {
                if (tagLogPointListMap.containsKey(tag)) {
                    tagLogPointListMap.get(tag).remove(id);
                }
            }
        }
    }

    private static void unMapLogPointFromAllTags(String id) {
        tagLogPointListMap.keySet().forEach(tag -> tagLogPointListMap.get(tag).remove(id));
    }
}
