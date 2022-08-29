package com.runsidekick.agent.core.util;

import com.runsidekick.agent.core.property.PropertyAccessor;
import com.runsidekick.agent.core.property.StandardPropertyAccessor;

import java.util.Map;

/**
 * Utility class for providing property access related stuff.
 *
 * @author serkan
 */
public final class PropertyUtils {

    /**
     * Property name of the <b>Sidekick</b> API key.
     */
    public static final String SIDEKICK_API_KEY_PROPERTY_NAME = "sidekick.apikey";

    /**
     * Property name of the <b>Sidekick</b> license key.
     */
    public static final String SIDEKICK_LICENSE_KEY_PROPERTY_NAME = "sidekick.licensekey";

    /**
     * {@link PropertyAccessor} instance to access global configuration properties.
     */
    public static final PropertyAccessor PROPERTY_ACCESSOR = StandardPropertyAccessor.DEFAULT;

    private PropertyUtils() {
    }

    /**
     * Gets the all properties.
     *
     * @return the all properties
     */
    public static Map<String, String> getProperties() {
        return PROPERTY_ACCESSOR.getProperties();
    }

    /**
     * Gets the <code>string</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    public static String getStringProperty(String propName) {
        return PROPERTY_ACCESSOR.getProperty(StringUtils.toLowerCase(propName));
    }

    /**
     * Gets the <code>string</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    public static String getStringProperty(String propName, String defaultPropValue) {
        return PROPERTY_ACCESSOR.getProperty(StringUtils.toLowerCase(propName), defaultPropValue);
    }

    /**
     * Gets the <code>boolean</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    public static Boolean getBooleanProperty(String propName) {
        return PROPERTY_ACCESSOR.getBooleanProperty(StringUtils.toLowerCase(propName));
    }

    /**
     * Gets the <code>boolean</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    public static Boolean getBooleanProperty(String propName, boolean defaultPropValue) {
        return PROPERTY_ACCESSOR.getBooleanProperty(StringUtils.toLowerCase(propName), defaultPropValue);
    }

    /**
     * Gets the <code>integer</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    public static Integer getIntegerProperty(String propName) {
        return PROPERTY_ACCESSOR.getIntegerProperty(StringUtils.toLowerCase(propName));
    }

    /**
     * Gets the <code>integer</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    public static Integer getIntegerProperty(String propName, int defaultPropValue) {
        return PROPERTY_ACCESSOR.getIntegerProperty(StringUtils.toLowerCase(propName), defaultPropValue);
    }

    /**
     * Gets the <code>long</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    public static Long getLongProperty(String propName) {
        return PROPERTY_ACCESSOR.getLongProperty(StringUtils.toLowerCase(propName));
    }

    /**
     * Gets the <code>long</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    public static Long getLongProperty(String propName, long defaultPropValue) {
        return PROPERTY_ACCESSOR.getLongProperty(StringUtils.toLowerCase(propName), defaultPropValue);
    }

    /**
     * Gets the <code>float</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    public static Float getFloatProperty(String propName) {
        return PROPERTY_ACCESSOR.getFloatProperty(StringUtils.toLowerCase(propName));
    }

    /**
     * Gets the <code>float</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    public static Float getFloatProperty(String propName, float defaultPropValue) {
        return PROPERTY_ACCESSOR.getFloatProperty(StringUtils.toLowerCase(propName), defaultPropValue);
    }

    /**
     * Gets the <code>double</code> typed property associated with the given property name.
     *
     * @param propName name of the property
     * @return the property value if it is exist, <code>null</code> otherwise
     */
    public static Double getDoubleProperty(String propName) {
        return PROPERTY_ACCESSOR.getDoubleProperty(StringUtils.toLowerCase(propName));
    }

    /**
     * Gets the <code>double</code> typed property associated with the given property name.
     *
     * @param propName          name of the property
     * @param defaultPropValue  default value of the property to be returned
     *                          if requested property is not exist
     * @return the property value if it is exist, default value otherwise
     */
    public static Double getDoubleProperty(String propName, double defaultPropValue) {
        return PROPERTY_ACCESSOR.getDoubleProperty(StringUtils.toLowerCase(propName), defaultPropValue);
    }

    /**
     * Gets the <b>Sidekick</b> API key.
     *
     * @return the <b>Sidekick</b> API key
     */
    public static String getApiKey() {
        return getStringProperty(SIDEKICK_API_KEY_PROPERTY_NAME);
    }

    /**
     * Gets the <b>Sidekick</b> license key.
     *
     * @return the <b>Sidekick</b> license key
     */
    public static String getLicenseKey() {
        return getStringProperty(SIDEKICK_LICENSE_KEY_PROPERTY_NAME);
    }

}
