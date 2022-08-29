package com.runsidekick.agent.probe.domain.impl;

import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.probe.condition.Condition;
import com.runsidekick.agent.probe.condition.ConditionContext;
import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.ProbeContext;
import org.slf4j.Logger;

/**
 * @author serkan
 */
public class ConditionAwareProbeAction<C extends ProbeContext> extends DelegatedProbeAction<C> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionAwareProbeAction.class);

    public ConditionAwareProbeAction(ProbeAction<C> action) {
        super(action);
    }

    protected boolean checkWhetherConditionOk(Probe probe, Class<?> clazz, Object obj, Object[] localVarValues) {
        C context = getContext();
        if (context != null) {
            Condition condition = context.getCondition();
            if (condition != null) {
                ConditionContext conditionContext = new ConditionContext(clazz, obj, localVarValues);
                return condition.evaluate(conditionContext);
            }
        }
        return true;
    }

    @Override
    public void onProbe(Probe probe, Class<?> clazz, Object obj, String methodName,
                        String[] localVarNames, Object[] localVarValues) {
        if (!checkWhetherConditionOk(probe, clazz, obj, localVarValues)) {
            return;
        }
        super.onProbe(probe, clazz, obj, methodName, localVarNames, localVarValues);
    }

}
