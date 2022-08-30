package com.runsidekick.agent.core.app.impl;

import com.runsidekick.agent.core.app.ApplicationInfo;
import com.runsidekick.agent.core.app.ApplicationInfoProvider;
import com.runsidekick.agent.core.util.ExceptionUtils;

import java.util.function.Supplier;

/**
 * {@link ApplicationInfoProvider} implementation which provides
 * {@link ApplicationInfo application information}
 * by delegating to supplied {@link ApplicationInfoProvider}.
 *
 * @author serkan
 */
public class SuppliedApplicationInfoProvider implements ApplicationInfoProvider {

    private final Supplier<ApplicationInfoProvider> appInfoProviderSupplier;

    public SuppliedApplicationInfoProvider(Supplier<ApplicationInfoProvider> appInfoProviderSupplier) {
        this.appInfoProviderSupplier = appInfoProviderSupplier;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        try {
            ApplicationInfoProvider applicationInfoProvider = appInfoProviderSupplier.get();
            if (applicationInfoProvider != null) {
                return applicationInfoProvider.getApplicationInfo();
            }
        } catch (Exception e) {
            ExceptionUtils.sneakyThrow(e);
        }
        return null;
    }

}
