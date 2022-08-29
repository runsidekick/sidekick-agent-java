package com.runsidekick.agent.all;

import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.PropertyUtils;
import com.runsidekick.agent.core.util.StringUtils;
import org.slf4j.Logger;

/**
 * Support class for agent related stuff.
 *
 * @author serkan
 */
public final class AgentSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentSupport.class);

    private static boolean initialized = false;

    public synchronized static void ensureInitialized() {
        if (!initialized) {
            doInitialize();
            initialized = true;
        }
    }

    private static void doInitialize() {
        String apiKey = PropertyUtils.getApiKey();
        boolean hasApiKey = StringUtils.hasValue(apiKey);

        if (!hasApiKey) {
            LOGGER.warn("API key is not set, so no monitoring data will be reported");
        }
    }

}
