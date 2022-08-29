package com.runsidekick.agent.core.initialize;

/**
 * @author serkan
 */
public class TestEnvironmentInitializer implements EnvironmentInitializer {

    private int initializeCounter;

    @Override
    public void initialize() {
        initializeCounter++;
    }

    public int getInitializeCounter() {
        return initializeCounter;
    }

}
