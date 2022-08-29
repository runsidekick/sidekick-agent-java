package com.runsidekick.agent.core.terminate;

import com.runsidekick.agent.core.instance.InstanceDiscovery;
import com.runsidekick.agent.core.logger.LoggerFactory;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Manager class to manage environment
 * {@link EnvironmentTerminator} related operations.
 *
 * @author serkan
 */
public final class EnvironmentTerminatorManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentTerminatorManager.class);

    private static boolean terminated = false;
    private static List<EnvironmentTerminator> ENVIRONMENT_TERMINATORS =
            InstanceDiscovery.instancesOf(EnvironmentTerminator.class);

    private EnvironmentTerminatorManager() {
    }

    public static synchronized void ensureTerminated() {
        if (!terminated) {
            try {
                terminate();
            } finally {
                terminated = true;
            }
        }
    }

    public static synchronized void registerEnvironmentTerminator(EnvironmentTerminator environmentTerminator) {
        ENVIRONMENT_TERMINATORS.add(environmentTerminator);
        Collections.sort(ENVIRONMENT_TERMINATORS, Comparator.comparingInt(EnvironmentTerminator::order));
    }

    public static synchronized void deregisterEnvironmentTerminator(EnvironmentTerminator environmentTerminator) {
        ENVIRONMENT_TERMINATORS.remove(environmentTerminator);
    }

    private static void terminate() {
        for (EnvironmentTerminator environmentTerminator : ENVIRONMENT_TERMINATORS) {
            try {
                long start = System.currentTimeMillis();
                environmentTerminator.terminate();
                LOGGER.debug(
                        String.format(
                                "Environment terminator %s has completed its termination in %d milliseconds",
                                environmentTerminator, System.currentTimeMillis() - start));
            } catch (Throwable t) {
                LOGGER.error(
                        String.format(
                                "Environment terminator %s has failed while termination because of %s",
                                environmentTerminator,
                        t));
            }
        }
    }

}
