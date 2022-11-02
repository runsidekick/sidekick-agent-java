package com.runsidekick.agent.api.tracepoint.integrations.junit5;

import com.runsidekick.agent.api.broker.publisher.EventPublisher;
import com.runsidekick.agent.api.tracepoint.TracePointAPI;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * @author serkan
 */
public class TracePointSnapshotExtension
        implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger LOGGER = Logger.getLogger(TracePointSnapshotExtension.class.getName());

    private static final ExtensionContext.Namespace EXTENSION_NAMESPACE =
            ExtensionContext.Namespace.create("TracePointSnapshotExtension");

    private final List<TracePoint> tracePoints;
    private final boolean takeSnapshotOnFail;

    private TracePointSnapshotExtension(List<TracePoint> tracePoints, boolean takeSnapshotOnFail) {
        this.tracePoints = tracePoints;
        this.takeSnapshotOnFail = takeSnapshotOnFail;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static final class TracePointSnapshotExtensionContext {

        private static final String STORE_KEY = "TracePointSnapshotExtensionContext";

        private final EventPublisher existingEventPublisher;
        private final SavingEventPublisher savingEventPublisher;
        private final Map<String, TracePoint> tracePointMap;

        private TracePointSnapshotExtensionContext(EventPublisher existingEventPublisher,
                                                   SavingEventPublisher savingEventPublisher,
                                                   Map<String, TracePoint> tracePointMap) {
            this.existingEventPublisher = existingEventPublisher;
            this.savingEventPublisher = savingEventPublisher;
            this.tracePointMap = tracePointMap;
        }

    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        EventPublisher eventPublisher = TracePointAPI.getEventPublisher();
        SavingEventPublisher savingEventPublisher = null;
        if (takeSnapshotOnFail) {
            savingEventPublisher = new SavingEventPublisher(eventPublisher);
            TracePointAPI.setEventPublisher(savingEventPublisher);
        }
        Map<String, TracePoint> tracePointMap = putTracePoints();

        TracePointSnapshotExtensionContext extContext =
                new TracePointSnapshotExtensionContext(eventPublisher, savingEventPublisher, tracePointMap);
        ExtensionContext.Store store = context.getStore(EXTENSION_NAMESPACE);
        store.put(TracePointSnapshotExtensionContext.STORE_KEY, extContext);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ExtensionContext.Store store = context.getStore(EXTENSION_NAMESPACE);
        TracePointSnapshotExtensionContext extContext =
                (TracePointSnapshotExtensionContext) store.get(TracePointSnapshotExtensionContext.STORE_KEY);
        if (extContext != null) {
            EventPublisher eventPublisher = extContext.existingEventPublisher;
            SavingEventPublisher savingEventPublisher = extContext.savingEventPublisher;
            Map<String, TracePoint> tracePointMap = extContext.tracePointMap;
            Optional<Throwable> errorOpt = context.getExecutionException();
            if (errorOpt.isPresent() && savingEventPublisher != null) {
                savingEventPublisher.flush();
            }
            removeTracePoints(tracePointMap);
            TracePointAPI.setEventPublisher(eventPublisher);
        }
    }

    private Map<String, TracePoint> putTracePoints() {
        Map<String, TracePoint> tracePointMap = new HashMap<>();
        for (TracePoint tracePoint : tracePoints) {
            try {
                String tracePointId = TracePointAPI.putTracePoint(
                        tracePoint.className, tracePoint.lineNo,
                        null, tracePoint.conditionExpression,
                        TracePointAPI.TRACEPOINT_MAX_EXPIRY_COUNT, TracePointAPI.TRACEPOINT_MAX_EXPIRY_SECS, true, false, null);
                tracePointMap.put(tracePointId, tracePoint);
            } catch (Exception ex) {
                LOGGER.severe(String.format(
                        "Unable to put tracepoint to class %s at line %d: %s",
                        tracePoint.className, tracePoint.lineNo, ex.getMessage()));
            }
        }
        return tracePointMap;
    }

    private void removeTracePoints(Map<String, TracePoint> tracePointMap) {
        for (Map.Entry<String, TracePoint> e : tracePointMap.entrySet()) {
            String tracePointId = e.getKey();
            TracePoint tracePoint = e.getValue();
            try {
                TracePointAPI.removeTracePoint(tracePointId);
            } catch (Exception ex) {
                LOGGER.severe(String.format(
                        "Unable to remove tracepoint from class %s at line %d: %d",
                        tracePoint.className, tracePoint.lineNo, ex.getMessage()));
            }
        }
    }

    private static class SavingEventPublisher implements EventPublisher {

        private final EventPublisher eventPublisher;
        private final ConcurrentLinkedQueue<String> eventQueue = new ConcurrentLinkedQueue();

        private SavingEventPublisher(EventPublisher eventPublisher) {
            this.eventPublisher = eventPublisher;
        }

        @Override
        public void publishEvent(String eventJson) {
            eventQueue.add(eventJson);
        }

        private void flush() {
            String eventJson;
            while ((eventJson = eventQueue.poll()) != null) {
                eventPublisher.publishEvent(eventJson);
            }
        }

    }

    public static class TracePoint {

        private final String className;
        private final int lineNo;
        private final String conditionExpression;

        private TracePoint(String className, int lineNo, String conditionExpression) {
            this.className = className;
            this.lineNo = lineNo;
            this.conditionExpression = conditionExpression;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private String className;
            private Integer lineNo;
            private String conditionExpression;

            private Builder() {
            }

            public Builder className(String className) {
                this.className = className;
                return this;
            }

            public Builder lineNo(int lineNo) {
                this.lineNo = lineNo;
                return this;
            }

            public Builder conditionExpression(String conditionExpression) {
                this.conditionExpression = conditionExpression;
                return this;
            }

            public TracePoint build() {
                if (className == null || lineNo == null) {
                    throw new IllegalArgumentException("Class name and line number must be specified");
                }
                return new TracePoint(className, lineNo, conditionExpression);
            }

        }

    }

    public static class Builder {

        private final List<TracePoint> tracePoints = new ArrayList<>();
        private boolean takeSnapshotOnFail = false;

        private Builder() {
        }

        public Builder addTracePoint(TracePoint tracePoint) {
            this.tracePoints.add(tracePoint);
            return this;
        }

        public Builder takeSnapshotOnFail(boolean takeSnapshotOnFail) {
            this.takeSnapshotOnFail = takeSnapshotOnFail;
            return this;
        }

        public TracePointSnapshotExtension build() {
            return new TracePointSnapshotExtension(tracePoints, takeSnapshotOnFail);
        }

    }

}
