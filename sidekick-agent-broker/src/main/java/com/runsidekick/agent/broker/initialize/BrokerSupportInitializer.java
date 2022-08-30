package com.runsidekick.agent.broker.initialize;

import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.core.initialize.EnvironmentInitializer;

/**
 * @author serkan
 */
public class BrokerSupportInitializer implements EnvironmentInitializer {

    @Override
    public void initialize() {
        BrokerManager.ensureInitialized();
    }

}
