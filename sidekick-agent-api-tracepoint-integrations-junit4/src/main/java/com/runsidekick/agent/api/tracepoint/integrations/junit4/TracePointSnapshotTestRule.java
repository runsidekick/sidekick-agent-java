package com.runsidekick.agent.api.tracepoint.integrations.junit4;

import com.runsidekick.agent.api.broker.publisher.EventPublisher;
import com.runsidekick.agent.api.tracepoint.TracePointAPI;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * @author serkan
 */
public class TracePointSnapshotTestRule implements TestRule {

    private static final Logger LOGGER = Logger.getLogger(TracePointSnapshotTestRule.class.getName());

    private final List<TracePoint> tracePoints;
    private final boolean takeSnapshotOnFail;

    private TracePointSnapshotTestRule(List<TracePoint> tracePoints, boolean takeSnapshotOnFail) {
        this.tracePoints = tracePoints;
        this.takeSnapshotOnFail = takeSnapshotOnFail;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                EventPublisher eventPublisher = TracePointAPI.getEventPublisher();
                SavingEventPublisher savingEventPublisher = null;
                if (takeSnapshotOnFail) {
                    savingEventPublisher = new SavingEventPublisher(eventPublisher);
                    TracePointAPI.setEventPublisher(savingEventPublisher);
                }
                Map<String, TracePoint> tracePointMap = putTracePoints();
                try {
                    base.evaluate();
                } catch (Throwable t) {
                    if (savingEventPublisher != null) {
                        savingEventPublisher.flush();
                    }
                    throw t;
                } finally {
                    removeTracePoints(tracePointMap);
                    TracePointAPI.setEventPublisher(eventPublisher);
                }
            }
        };
    }

    private Map<String, TracePoint> putTracePoints() {
        Map<String, TracePoint> tracePointMap = new HashMap<>();
        for (TracePoint tracePoint : tracePoints) {
            try {
                String tracePointId = TracePointAPI.putTracePoint(
                        tracePoint.className, tracePoint.lineNo,
                        null, tracePoint.conditionExpression,
                        TracePointAPI.TRACEPOINT_MAX_EXPIRY_COUNT, TracePointAPI.TRACEPOINT_MAX_EXPIRY_SECS, true, false);
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

        public TracePointSnapshotTestRule build() {
            return new TracePointSnapshotTestRule(tracePoints, takeSnapshotOnFail);
        }

    }

}
