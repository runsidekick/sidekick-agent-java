package com.runsidekick.agent.core.util.executor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author serkan
 */
public class ManagedScheduledThreadPoolExecutor
        extends ScheduledThreadPoolExecutor
        implements SidekickExecutor {

    public ManagedScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public ManagedScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public ManagedScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public ManagedScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory,
                                              RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

}
