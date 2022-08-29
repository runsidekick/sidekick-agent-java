package com.runsidekick.agent.tracepoint.internal;

import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.condition.Condition;
import com.runsidekick.agent.probe.domain.ProbeContext;

import java.util.concurrent.ScheduledFuture;

/**
 * @author serkan
 */
class TracePointContext implements ProbeContext {

    final Probe probe;
    final String id;
    final String conditionExpression;
    final int expireSecs;
    final int expireCount;
    final boolean enableTracing;
    final Condition condition;
    volatile ScheduledFuture expireFuture;
    volatile boolean disabled;
    volatile boolean removed;

    TracePointContext(Probe probe, String id,
                      String conditionExpression, int expireSecs, int expireCount, boolean enableTracing,
                      Condition condition, boolean disabled) {
        this.probe = probe;
        this.id = id;
        this.conditionExpression = conditionExpression;
        this.expireSecs = expireSecs;
        this.expireCount = expireCount;
        this.enableTracing = enableTracing;
        this.condition = condition;
        this.disabled = disabled;
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public int getExpireCount() {
        return expireCount;
    }

    @Override
    public void expire() {
        cancelExpireScheduleIfExist();
        expireFuture = TracePointManager.scheduledExpireTask(this);
    }

    void cancelExpireScheduleIfExist() {
        ScheduledFuture expireFuture = this.expireFuture;
        if (expireFuture != null) {
            try {
                expireFuture.cancel(true);
            } finally {
                this.expireFuture = null;
            }
        }
    }

}
