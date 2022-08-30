package com.runsidekick.agent.core.property;

import com.runsidekick.agent.core.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * System property based {@link PropertyAccessor} implementation
 * which searches/loads properties from system properties.
 *
 * @author serkan
 */
public class SystemPropertyAwarePropertyAccessor implements PropertyAccessor {

    @Override
    public String getProperty(String propName) {
        return System.getProperty(propName);
    }

    @Override
    public Map<String, String> getProperties() {
        Properties properties = System.getProperties();
        Set<String> propNames = properties.stringPropertyNames();
        Map<String, String> propMap = new HashMap<>(propNames.size());
        for (String propName : properties.stringPropertyNames()) {
            propMap.put(StringUtils.toLowerCase(propName), properties.getProperty(propName));
        }
        return propMap;
    }

}
