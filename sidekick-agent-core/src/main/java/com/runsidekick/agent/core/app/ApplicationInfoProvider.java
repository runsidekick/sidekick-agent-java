package com.runsidekick.agent.core.app;

import com.runsidekick.agent.core.util.EnvironmentUtils;

/**
 * Interface to provide {@link ApplicationInfo}
 * which contains application specific information
 * such as application name, type, id, etc ...
 *
 * @author serkan
 */
public interface ApplicationInfoProvider {

    /**
     * Runtime of the application which represents <code>JAVA</code> runtime.
     */
    String APPLICATION_RUNTIME = "java";

    /**
     * Runtime version of the application which represents <code>JVM</code> version.
     */
    String APPLICATION_RUNTIME_VERSION = EnvironmentUtils.JVM_VERSION;

    /**
     * Provides {@link ApplicationInfo}.
     *
     * @return the provided {@link ApplicationInfo}
     */
    ApplicationInfo getApplicationInfo();

}
