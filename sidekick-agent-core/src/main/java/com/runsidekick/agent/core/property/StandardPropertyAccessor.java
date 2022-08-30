package com.runsidekick.agent.core.property;

import com.runsidekick.agent.core.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard {@link PropertyAccessor} implementation to be used for common cases.
 *
 * @author serkan
 */
public class StandardPropertyAccessor
        extends CombinedPropertyAccessor
        implements MutablePropertyAccessor {

    private static final String DEFAULT_PROPERTIES_CONFIG_FILE_NAME = "sidekick-config.properties";
    private static final String DEFAULT_YAML_CONFIG_FILE_NAME = "sidekick-config.yml";
    private static final String PROPERTIES_CONFIG_FILE_NAME;

    static {
        String propConfigFileName = System.getProperty("sidekick.agent.property.file");
        if (propConfigFileName == null) {
            propConfigFileName = System.getenv("SIDEKICK_AGENT_PROPERTY_FILE");
        }
        PROPERTIES_CONFIG_FILE_NAME = propConfigFileName;
    }

    public static final StandardPropertyAccessor DEFAULT = new StandardPropertyAccessor();

    public StandardPropertyAccessor() {
        super(new ConcurrentHashMap<>(), // because this property accessor is mutable
              createStandardPropertyAccessors(null, ProfileProvider.getProfile()));
    }

    public StandardPropertyAccessor(String propFileName) {
        super(new ConcurrentHashMap<>(), // because this property accessor is mutable
              createStandardPropertyAccessors(propFileName, ProfileProvider.getProfile()));
    }

    public StandardPropertyAccessor(String propFileName, String profileName) {
        super(new ConcurrentHashMap<>(), // because this property accessor is mutable
              createStandardPropertyAccessors(propFileName, profileName));
    }

    private static List<PropertyAccessor> createStandardPropertyAccessors(String propFileName, String profileName) {
        List<PropertyAccessor> propertyAccessors = new ArrayList<>();

        propertyAccessors.add(ProvidedPropertyAccessor.INSTANCE);

        propertyAccessors.add(new SystemPropertyAwarePropertyAccessor());
        propertyAccessors.add(new SystemEnvironmentAwarePropertyAccessor());

        if (propFileName != null) {
            propertyAccessors.add(new UserHomeAwarePropertyAccessor(propFileName, profileName));
            propertyAccessors.add(new ClassPathAwarePropertyAccessor(propFileName, profileName));
        }

        if (PROPERTIES_CONFIG_FILE_NAME != null) {
            propertyAccessors.add(new UserHomeAwarePropertyAccessor(PROPERTIES_CONFIG_FILE_NAME, profileName));
            propertyAccessors.add(new ClassPathAwarePropertyAccessor(PROPERTIES_CONFIG_FILE_NAME, profileName));
        } else {
            propertyAccessors.add(new UserHomeAwarePropertyAccessor(DEFAULT_PROPERTIES_CONFIG_FILE_NAME, profileName));
            propertyAccessors.add(new ClassPathAwarePropertyAccessor(DEFAULT_PROPERTIES_CONFIG_FILE_NAME, profileName));
            propertyAccessors.add(new UserHomeAwarePropertyAccessor(DEFAULT_YAML_CONFIG_FILE_NAME, profileName));
            propertyAccessors.add(new ClassPathAwarePropertyAccessor(DEFAULT_YAML_CONFIG_FILE_NAME, profileName));
        }

        return propertyAccessors;
    }

    @Override
    public String putProperty(String propName, String propValue) {
        return props.put(StringUtils.toLowerCase(propName), propValue);
    }

    @Override
    public String putPropertyIfAbsent(String propName, String propValue) {
        return props.putIfAbsent(StringUtils.toLowerCase(propName), propValue);
    }

    @Override
    public String removeProperty(String propName) {
        return props.remove(StringUtils.toLowerCase(propName));
    }

}
