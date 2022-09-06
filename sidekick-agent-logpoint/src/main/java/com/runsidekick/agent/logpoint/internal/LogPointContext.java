package com.runsidekick.agent.logpoint.internal;

import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.condition.Condition;
import com.runsidekick.agent.probe.domain.ProbeContext;

import java.util.concurrent.ScheduledFuture;

/**
 * @author yasin
 */
class LogPointContext implements ProbeContext {

    final Probe probe;
    final String id;
    final String logExpression;
    final boolean stdoutEnabled;
    final String logLevel;
    final String conditionExpression;
    final int expireSecs;
    final int expireCount;
    final Condition condition;
    volatile ScheduledFuture expireFuture;
    volatile boolean disabled;
    volatile boolean removed;
    volatile boolean predefined;

    LogPointContext(Probe probe, String id,
                    String logExpression, String conditionExpression, int expireSecs, int expireCount,
                    Condition condition, boolean disabled, boolean stdoutEnabled, String logLevel, boolean predefined) {
        this.probe = probe;
        this.id = id;
        this.logExpression = logExpression;
        this.conditionExpression = conditionExpression;
        this.expireSecs = expireSecs;
        this.expireCount = expireCount;
        this.condition = condition;
        this.disabled = disabled;
        this.stdoutEnabled = stdoutEnabled;
        this.logLevel = logLevel;
        this.predefined = predefined;
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
        expireFuture = LogPointManager.scheduledExpireTask(this);
    }

    @Override
    public boolean isPredefined() {
        return predefined;
    }

    public String getLogExpression() {
        return logExpression;
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
