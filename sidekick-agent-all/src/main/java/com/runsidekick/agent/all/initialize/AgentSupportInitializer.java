package com.runsidekick.agent.all.initialize;

import com.runsidekick.agent.all.AgentSupport;
import com.runsidekick.agent.core.initialize.EnvironmentInitializer;

/**
 * {@link EnvironmentInitializer} implementation for initialization
 * of Sidekick agent.
 *
 * @author serkan
 */
public class AgentSupportInitializer implements EnvironmentInitializer {

    @Override
    public void initialize() {
        AgentSupport.ensureInitialized();
    }

    @Override
    public int order() {
        return HIGHEST;
    }

}
