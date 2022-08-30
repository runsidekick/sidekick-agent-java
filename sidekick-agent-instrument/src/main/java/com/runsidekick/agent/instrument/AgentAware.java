package com.runsidekick.agent.instrument;

import com.runsidekick.agent.core.entity.Ordered;

import java.lang.instrument.Instrumentation;

/**
 * Interface for implementations to be notified when <b>Sidekick</b> agent is started
 * from command line as JVM argument.
 *
 * @author serkan
 */
public interface AgentAware extends Ordered {

    /**
     * Called when agent is started
     *
     * @param arguments         the passed arguments to the agent
     * @param instrumentation   the {@link Instrumentation} instance
     */
    void onAgentStart(String arguments, Instrumentation instrumentation);

}
