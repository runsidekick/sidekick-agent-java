package com.runsidekick.agent.probe.condition.value;

/**
 * @author serkan
 */
public abstract class BasePlaceholderValueProvider<V> implements PlaceholderValueProvider<V> {

    protected final String placeholderName;
    protected final Class<V> valueType;

    public BasePlaceholderValueProvider(String placeholderName, Class<V> valueType) {
        this.placeholderName = placeholderName;
        this.valueType = valueType;
    }

    @Override
    public String getPlaceholderName() {
        return placeholderName;
    }

    @Override
    public Class<V> getValueType() {
        return valueType;
    }

}
