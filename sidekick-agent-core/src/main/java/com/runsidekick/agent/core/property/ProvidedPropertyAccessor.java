package com.runsidekick.agent.core.property;

import com.runsidekick.agent.core.property.provider.PropertyProviderManager;
import com.runsidekick.agent.core.property.provider.PropertyProvider;

import java.util.Map;

/**
 * {@link PropertyAccessor} implementation which provides properties
 * over registered {@link PropertyProvider}s.
 *
 * @author serkan
 */
public final class ProvidedPropertyAccessor implements PropertyAccessor {

    public static final ProvidedPropertyAccessor INSTANCE = new ProvidedPropertyAccessor();

    private ProvidedPropertyAccessor() {
    }

    @Override
    public String getProperty(String propName) {
        return PropertyProviderManager.lookupProperty(propName);
    }

    @Override
    public Map<String, String> getProperties() {
        return PropertyProviderManager.getAllProvidedProperties();
    }

}
