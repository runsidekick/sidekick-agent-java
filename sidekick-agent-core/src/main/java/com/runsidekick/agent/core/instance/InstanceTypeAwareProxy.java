package com.runsidekick.agent.core.instance;

/**
 * Interface to represent proxies which are aware of
 * interface {@link Class}es of the underlying proxied instances.
 *
 * @author serkan
 */
public interface InstanceTypeAwareProxy {

    /**
     * Gets the interface {@link Class} of the proxied instance.
     *
     * @return the interface {@link Class} of the proxied instance
     */
    Class getInstanceType();

}
