package com.runsidekick.agent.probe.domain.impl;

import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.ProbeContext;

/**
 * @author serkan
 */
public abstract class DelegatedProbeAction<C extends ProbeContext> implements ProbeAction<C> {

    private final ProbeAction<C> action;

    public DelegatedProbeAction(ProbeAction<C> action) {
        this.action = action;
    }

    @Override
    public String id() {
        return action.id();
    }

    @Override
    public C getContext() {
        return action.getContext();
    }

    @Override
    public boolean isDisabled() {
        return action.isDisabled();
    }

    @Override
    public void onProbe(Probe probe, Class<?> clazz, Object obj, String methodName,
                        String[] localVarNames, Object[] localVarValues) {
        action.onProbe(probe, clazz, obj, methodName, localVarNames, localVarValues);
    }

    @Override
    public C unwrap(Class<C> clazz) {
        if (action != null) {
            return action.unwrap(clazz);
        } else {
            return ProbeAction.super.unwrap(clazz);
        }
    }

}
