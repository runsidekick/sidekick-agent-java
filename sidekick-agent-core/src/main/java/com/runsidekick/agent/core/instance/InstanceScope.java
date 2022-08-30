package com.runsidekick.agent.core.instance;

/**
 * Represents scope of provided instances.
 *
 * @author serkan
 */
public enum InstanceScope {

    /**
     * Gives singleton instance across all application (in fact classloader)
     */
    GLOBAL,

    /**
     * Gives thread specific instance for each thread
     */
    THREAD_LOCAL,

    /**
     * Gives thread specific instance for hierarchically connected threads (parent/child)
     */
    INHERITABLE_THREAD_LOCAL,

    /**
     * Gives new fresh instance for every request
     */
    PROTOTYPE;

}
