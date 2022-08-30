package com.runsidekick.agent.core.instance;

/**
 * Interface to represent proxies which are aware of
 * implementation {@link Class}es of the underlying proxied instances.
 *
 * @author serkan
 */
public interface InstanceClassAwareProxy {

    /**
     * Gets the implementation {@link Class} of the proxied instance.
     *
     * @return the implementation {@link Class} of the proxied instance
     */
    Class getInstanceClass();

}
