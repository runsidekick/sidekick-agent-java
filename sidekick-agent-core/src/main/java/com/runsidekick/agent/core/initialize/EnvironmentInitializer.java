package com.runsidekick.agent.core.initialize;

import com.runsidekick.agent.core.entity.Ordered;

/**
 * Interface for types to be initialized on startup.
 *
 * @author serkan
 */
public interface EnvironmentInitializer extends Ordered {

    /**
     * Executes pre-initialization logic.
     */
    default void preInitialize() {
    }

    /**
     * Executes initialization logic.
     */
    void initialize();

}
