package com.runsidekick.agent.probe.initialize;

import com.runsidekick.agent.core.initialize.EnvironmentInitializer;
import com.runsidekick.agent.probe.ProbeSupport;

/**
 * @author serkan
 */
public class ProbeSupportInitializer implements EnvironmentInitializer {

    @Override
    public void initialize() {
        ProbeSupport.ensureInitialized();
    }

}
