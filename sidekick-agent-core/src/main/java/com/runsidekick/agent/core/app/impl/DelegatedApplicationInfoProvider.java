package com.runsidekick.agent.core.app.impl;

import com.runsidekick.agent.core.app.ApplicationInfo;
import com.runsidekick.agent.core.app.ApplicationInfoProvider;

/**
 * {@link ApplicationInfoProvider} implementation which delegates to
 * specified {@link ApplicationInfoProvider} instance.
 *
 * @author serkan
 */
public class DelegatedApplicationInfoProvider implements ApplicationInfoProvider {

    private volatile ApplicationInfoProvider delegatedApplicationInfoProvider;

    public DelegatedApplicationInfoProvider(ApplicationInfoProvider delegatedApplicationInfoProvider) {
        this.delegatedApplicationInfoProvider = delegatedApplicationInfoProvider;
    }

    public ApplicationInfoProvider getDelegatedApplicationInfoProvider() {
        return delegatedApplicationInfoProvider;
    }

    public void setDelegatedApplicationInfoProvider(ApplicationInfoProvider delegatedApplicationInfoProvider) {
        this.delegatedApplicationInfoProvider = delegatedApplicationInfoProvider;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        ApplicationInfoProvider applicationInfoProvider = delegatedApplicationInfoProvider;
        if (applicationInfoProvider != null) {
            return applicationInfoProvider.getApplicationInfo();
        } else {
            return null;
        }
    }

}
