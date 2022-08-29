package com.runsidekick.agent.logpoint.initialize;

import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.core.initialize.EnvironmentInitializer;

/**
 * @author yasin
 */
public class LogPointSupportInitializer implements EnvironmentInitializer {

    @Override
    public void initialize() {
        LogPointSupport.ensureInitialized();
    }

}
