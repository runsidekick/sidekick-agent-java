package com.runsidekick.agent.core.property;

import com.runsidekick.agent.core.property.provider.PropertyProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author serkan
 */
public class TestPropertyProvider implements PropertyProvider {

    private final Map<String, String> props = new HashMap<String, String>();

    public TestPropertyProvider() {
        props.put("key1", "value1");
    }

    @Override
    public String getProperty(String propName) {
        return props.get(propName);
    }

    @Override
    public Map<String, String> getProperties() {
        return props;
    }

    @Override
    public List<String> getProvidedPropertyNames() {
        return Arrays.asList("key1");
    }

}
