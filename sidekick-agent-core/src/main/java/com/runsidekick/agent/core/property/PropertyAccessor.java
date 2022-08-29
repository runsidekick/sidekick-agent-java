package com.runsidekick.agent.core.property;

import java.util.Map;

/**
 * Interface for implementations which provide properties.
 *
 * @author serkan
 */
public interface PropertyAccessor {

    /**
     * Gets the property associated with given property name.
     *
     * @param propName the name of property to be retrieved
     * @return the property associated with given property name
     */
    String getProperty(String propName);

    /**
     * Gets the all properties.
     *
     * @return the all properties
     */
    Map<String, String> getProperties();

    /**
     * Checks whether there is existing property associated with given property name.
     *
     * @param propName name of the property to be checked whether it is exist
     * @return <code>true</code> if property is exist, <code>false</code> otherwise
     */
    default boolean hasProperty(String propName) {
        return getProperty(propName) != null;
    }

    /**
     * Gets the <code>string</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    default String getProperty(String propName, String defaultPropValue) {
        String propValue = getProperty(propName);
        if (propValue != null) {
            return propValue;
        }
        return defaultPropValue;
    }

    /**
     * Gets the <code>boolean</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    default Boolean getBooleanProperty(String propName) {
        String propValue = getProperty(propName);
        if (propValue == null) {
            return null;
        }
        return Boolean.parseBoolean(propValue);
    }

    /**
     * Gets the <code>boolean</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    default Boolean getBooleanProperty(String propName, boolean defaultPropValue) {
        String propValue = getProperty(propName);
        if (propValue == null) {
            return defaultPropValue;
        }
        return Boolean.parseBoolean(propValue);
    }

    /**
     * Gets the <code>integer</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    default Integer getIntegerProperty(String propName) {
        String propValue = getProperty(propName);
        if (propValue == null) {
            return null;
        }
        return Integer.parseInt(propValue);
    }

    /**
     * Gets the <code>integer</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    default Integer getIntegerProperty(String propName, int defaultPropValue) {
        String propValue = getProperty(propName);
        if (propValue == null) {
            return defaultPropValue;
        }
        return Integer.parseInt(propValue);
    }

    /**
     * Gets the <code>long</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    default Long getLongProperty(String propName) {
        String propValue = getProperty(propName);
        if (propValue == null) {
            return null;
        }
        return Long.parseLong(propValue);
    }

    /**
     * Gets the <code>long</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    default Long getLongProperty(String propName, long defaultPropValue) {
        String propValue = getProperty(propName);
        if (propValue == null) {
            return defaultPropValue;
        }
        return Long.parseLong(propValue);
    }

    /**
     * Gets the <code>float</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    default Float getFloatProperty(String propName) {
        String propValue = getProperty(propName);
        if (propValue == null) {
            return null;
        }
        return Float.parseFloat(propValue);
    }

    /**
     * Gets the <code>float</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    default Float getFloatProperty(String propName, float defaultPropValue) {
        String propValue = getProperty(propName);
        if (propValue == null) {
            return defaultPropValue;
        }
        return Float.parseFloat(propValue);
    }

    /**
     * Gets the <code>double</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    default Double getDoubleProperty(String propName) {
        String propValue = getProperty(propName);
        if (propValue == null) {
            return null;
        }
        return Double.parseDouble(propValue);
    }

    /**
     * Gets the <code>double</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    default Double getDoubleProperty(String propName, double defaultPropValue) {
        String propValue = getProperty(propName);
        if (propValue == null) {
            return defaultPropValue;
        }
        return Double.parseDouble(propValue);
    }

}
