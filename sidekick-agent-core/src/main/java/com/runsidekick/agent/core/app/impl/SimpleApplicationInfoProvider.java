package com.runsidekick.agent.core.app.impl;

import com.runsidekick.agent.core.app.ApplicationInfo;
import com.runsidekick.agent.core.app.ApplicationInfoProvider;

import java.util.Map;

/**
 * Simple {@link ApplicationInfoProvider} implementation
 * which provided {@link ApplicationInfo application information}
 * from given values.
 *
 * @author serkan
 */
public class SimpleApplicationInfoProvider implements ApplicationInfoProvider {

    private final String applicationId;
    private final String applicationInstanceId;
    private final String applicationDomainName;
    private final String applicationClassName;
    private final String applicationName;
    private final String applicationVersion;
    private final String applicationStage;
    private final Map<String, Object> applicationTags;

    public SimpleApplicationInfoProvider(String applicationId, String applicationInstanceId,
                                         String applicationDomainName, String applicationClassName,
                                         String applicationName, String applicationVersion, String applicationStage,
                                         Map<String, Object> applicationTags) {
        this.applicationId = applicationId;
        this.applicationInstanceId = applicationInstanceId;
        this.applicationDomainName = applicationDomainName;
        this.applicationClassName = applicationClassName;
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.applicationStage = applicationStage;
        this.applicationTags = applicationTags;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        ApplicationInfo applicationInfo =
                new ApplicationInfo(
                        applicationId,
                        applicationInstanceId,
                        applicationDomainName,
                        applicationClassName,
                        applicationName,
                        applicationVersion,
                        applicationStage,
                        APPLICATION_RUNTIME,
                        APPLICATION_RUNTIME_VERSION,
                        applicationTags);
        return applicationInfo;
    }

}
