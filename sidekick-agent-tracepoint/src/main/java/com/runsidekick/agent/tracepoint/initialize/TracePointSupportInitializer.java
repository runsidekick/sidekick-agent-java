package com.runsidekick.agent.tracepoint.initialize;

import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.core.initialize.EnvironmentInitializer;

/**
 * @author serkan
 */
public class TracePointSupportInitializer implements EnvironmentInitializer {

    @Override
    public void initialize() {
        TracePointSupport.ensureInitialized();
    }

}
