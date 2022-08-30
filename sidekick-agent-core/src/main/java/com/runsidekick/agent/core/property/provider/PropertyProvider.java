package com.runsidekick.agent.core.property.provider;

import com.runsidekick.agent.core.entity.Ordered;
import com.runsidekick.agent.core.property.PropertyAccessor;

import java.util.List;

/**
 * Sub-type of {@link PropertyProvider} which provides custom properties.
 *
 * @author serkan
 */
public interface PropertyProvider
        extends PropertyAccessor, Ordered {

    /**
     * Gets names of the provided property names.
     *
     * @return names of the provided property names
     */
    List<String> getProvidedPropertyNames();

}
