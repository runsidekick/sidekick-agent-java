package com.runsidekick.agent.core.terminate;

import com.runsidekick.agent.core.entity.Ordered;

/**
 * Interface for types to be terminated on shutdown.
 *
 * @author serkan
 */
public interface EnvironmentTerminator extends Ordered {

    /**
     * Executes termination logic.
     */
    void terminate();

}
