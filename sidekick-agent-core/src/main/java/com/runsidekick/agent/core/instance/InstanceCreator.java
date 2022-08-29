package com.runsidekick.agent.core.instance;

/**
 * Creates instances for given type.
 *
 * @author serkan
 */
public interface InstanceCreator {

    /**
     * Creates instances for given type.
     *
     * @param clazz {@link Class} of the instance to be created
     * @param <T> generic type of the instance
     * @return the created instance
     */
    <T> T create(Class<T> clazz);

}
