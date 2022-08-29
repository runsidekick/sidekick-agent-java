package com.runsidekick.agent.logpoint.internal;

/**
 * @author yasin
 */
class LogPointExpireTask implements Runnable {

    private final LogPointContext context;

    LogPointExpireTask(LogPointContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        context.expireFuture = null;
        LogPointManager.expireLogPoint(context);
    }

}
