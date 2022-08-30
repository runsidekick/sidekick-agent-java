package com.runsidekick.agent.core.entity;

/**
 * Interface for implementations which can be activated/deactivated.
 *
 * @author serkan
 */
public interface Activatable {

    /**
     * Returns activation status whether it is active.
     *
     * @return <code>true</code> if active, <code>false</code> otherwise
     */
    boolean isActive();

    /**
     * Activates.
     */
    void activate();

    /**
     * Deactivates.
     */
    void deactivate();

}
