package com.runsidekick.agent.core.initialize;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for sub-types of {@link EnvironmentInitializer} which can be initialized asynchronously.
 *
 * @author serkan
 */
public interface EnvironmentAsyncInitializer extends EnvironmentInitializer {

    /**
     * Called before initialization.
     */
    default void preInitializeAsync() {
    }

    /**
     * Executes initialization logic in async way.
     *
     * @return {@link CompletableFuture} future to check whether or not initialization has completed
     */
    CompletableFuture initializeAsync();

}
