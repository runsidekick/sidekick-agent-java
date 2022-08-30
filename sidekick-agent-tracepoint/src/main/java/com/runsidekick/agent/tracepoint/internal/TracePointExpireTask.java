package com.runsidekick.agent.tracepoint.internal;

/**
 * @author serkan
 */
class TracePointExpireTask implements Runnable {

    private final TracePointContext context;

    TracePointExpireTask(TracePointContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        context.expireFuture = null;
        TracePointManager.expireTracePoint(context);
    }

}
