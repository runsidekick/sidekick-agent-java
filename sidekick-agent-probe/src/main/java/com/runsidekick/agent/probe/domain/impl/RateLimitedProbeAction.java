package com.runsidekick.agent.probe.domain.impl;

import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.probe.ProbeSupport;
import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.ProbeContext;
import com.runsidekick.agent.probe.event.ProbeRateLimitEvent;
import com.runsidekick.agent.probe.ratelimit.RateLimitResult;
import com.runsidekick.agent.probe.ratelimit.RateLimiter;
import org.slf4j.Logger;

/**
 * @author serkan
 */
public class RateLimitedProbeAction<C extends ProbeContext> extends DelegatedProbeAction<C> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitedProbeAction.class);

    private final RateLimiter rateLimiter = new RateLimiter();

    public RateLimitedProbeAction(ProbeAction<C> action) {
        super(action);
    }

    protected boolean checkWhetherRateLimited(Probe probe) {
        RateLimitResult rateLimitResult = rateLimiter.checkRateLimit(System.currentTimeMillis());
        if (rateLimitResult == RateLimitResult.HIT) {
            LOGGER.warn(
                    "Probe from class {} on line {} from client {} has just hit rate limit. " +
                            "So subsequent probe hits will be skipped.",
                    probe.getClassName(), probe.getLineNo(), probe.getClient());
            ProbeRateLimitEvent probeRateLimitEvent =
                    new ProbeRateLimitEvent(probe.getClassName(), probe.getLineNo(), probe.getClient());
            ProbeSupport.publishProbeEvent(probeRateLimitEvent);
        }
        return rateLimitResult == RateLimitResult.EXCEEDED;
    }

    @Override
    public void onProbe(Probe probe, Class<?> clazz, Object obj, String methodName,
                        String[] localVarNames, Object[] localVarValues) {
        if (checkWhetherRateLimited(probe)) {
            return;
        }
        super.onProbe(probe, clazz, obj, methodName, localVarNames, localVarValues);
    }

}
