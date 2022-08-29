package com.runsidekick.agent.core.property;

import java.util.*;

/**
 * {@link PropertyAccessor} implementation which combines multiple
 * {@link PropertyAccessor}s.
 *
 * @author serkan
 */
public class CombinedPropertyAccessor implements PropertyAccessor {

    protected final Map<String, String> props;
    protected final List<PropertyAccessor> propertyAccessors;

    protected CombinedPropertyAccessor(Map<String, String> props,
                                       PropertyAccessor... propertyAccessors) {
        this.props = props;
        this.propertyAccessors = Arrays.asList(propertyAccessors);
        for (int i = propertyAccessors.length - 1; i >= 0; i--) {
            PropertyAccessor propertyAccessor = propertyAccessors[i];
            props.putAll(propertyAccessor.getProperties());
        }
    }

    public CombinedPropertyAccessor(PropertyAccessor... propertyAccessors) {
        this(new HashMap<>(), propertyAccessors);
    }

    protected CombinedPropertyAccessor(Map<String, String> props,
                                       List<PropertyAccessor> propertyAccessors) {
        this.props = props;
        this.propertyAccessors = propertyAccessors;
        for (int i = propertyAccessors.size() - 1; i >= 0; i--) {
            PropertyAccessor propertyAccessor = propertyAccessors.get(i);
            props.putAll(propertyAccessor.getProperties());
        }
    }

    public CombinedPropertyAccessor(List<PropertyAccessor> propertyAccessors) {
        this(new HashMap<>(), propertyAccessors);
    }

    @Override
    public String getProperty(String propName) {
        return props.get(propName);
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(props);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<PropertyAccessor> propertyAccessors = new ArrayList<>();

        private Builder() {
        }

        public Builder add(PropertyAccessor propertyAccessor) {
            propertyAccessors.add(propertyAccessor);
            return this;
        }

        public CombinedPropertyAccessor build() {
            return new CombinedPropertyAccessor(propertyAccessors);
        }

    }

}
