package com.runsidekick.agent.core.instance;

import com.runsidekick.agent.core.entity.Ordered;

/**
 * Interface for implementations which provide base path
 * for service definition files.
 *
 * @author serkan
 */
public interface InstanceDefinitionPathProvider extends Ordered {

    /**
     * Gets the path contains instance definition files.
     *
     * @return the path contains instance definition files
     *         <code>null</code> if default path (<code>META-INF/services/</code>)
     *         should be used
     */
    String getPath();

}
