package com.runsidekick.agent.core.factory;

/**
 * Interface for implementations which create instances.
 *
 * @param <T> type of the created instances
 *
 * @author serkan
 */
public interface Factory<T> {

    /**
     * Creates (or provides) and returns the requested instance.
     *
     * @return the requested instance
     */
    T create();

}
