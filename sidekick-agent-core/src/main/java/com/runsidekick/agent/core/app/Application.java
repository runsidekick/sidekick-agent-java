package com.runsidekick.agent.core.app;

import com.runsidekick.agent.core.util.InstanceUtils;
import com.runsidekick.agent.core.instance.InstanceScope;
import com.runsidekick.agent.core.app.impl.DelegatedApplicationInfoProvider;
import com.runsidekick.agent.core.app.impl.PropertyAwareApplicationInfoProvider;

/**
 * Mediator class for common application related stuff.
 *
 * @author serkan
 */
public final class Application {

    private static final DelegatedApplicationInfoProvider delegatedApplicationInfoProvider =
            new DelegatedApplicationInfoProvider(
                    InstanceUtils.getInstanceFromProperties(
                            "sidekick.agent.application.application-info-provider-factory",
                            "sidekick.agent.application.application-info-provider",
                            InstanceScope.GLOBAL,
                            new PropertyAwareApplicationInfoProvider()));

    private Application() {
    }

    public static ApplicationInfoProvider getApplicationInfoProvider() {
        return delegatedApplicationInfoProvider.getDelegatedApplicationInfoProvider();
    }

    public static void setApplicationInfoProvider(ApplicationInfoProvider applicationInfoProvider) {
        delegatedApplicationInfoProvider.setDelegatedApplicationInfoProvider(applicationInfoProvider);
    }

    public static ApplicationInfo getApplicationInfo() {
        return delegatedApplicationInfoProvider.getApplicationInfo();
    }

}
