package com.runsidekick.agent.probe.domain.impl;

import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.ProbeContext;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author serkan
 */
public class ExpiringProbeAction<C extends ProbeContext> extends DelegatedProbeAction<C> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiringProbeAction.class);

    private final AtomicLong callCounter = new AtomicLong(0L);

    public ExpiringProbeAction(ProbeAction<C> action) {
        super(action);
    }

    protected boolean checkWhetherExpired(Probe probe) {
        C context = getContext();
        if (context != null) {
            if (!context.hasTag()) {
                int expireCount = context.getExpireCount();
                if (expireCount > 0) {
                    long callCount = callCounter.incrementAndGet();
                    if (callCount >= expireCount) {
                        if (callCount == expireCount) {
                            context.expire();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onProbe(Probe probe, Class<?> clazz, Object obj, String methodName,
                        String[] localVarNames, Object[] localVarValues) {
        if (checkWhetherExpired(probe)) {
            return;
        }
        super.onProbe(probe, clazz, obj, methodName, localVarNames, localVarValues);
    }

}
