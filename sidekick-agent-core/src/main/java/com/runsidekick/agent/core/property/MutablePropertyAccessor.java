package com.runsidekick.agent.core.property;

/**
 * Interface for mutable {@link PropertyAccessor} implementations.
 *
 * @author serkan
 */
public interface MutablePropertyAccessor
        extends PropertyAccessor {

    /**
     * Puts the property associated with given property name.
     *
     * @param propName  the name of property to be put
     * @param propValue the value of property to be put
     * @return the old value of the property if it is exist,
     *         <code>null</code> otherwise
     */
    String putProperty(String propName, String propValue);

    /**
     * Puts the property associated with given property name
     * if and only if it is not exist.
     *
     * @param propName  the name of property to be put
     * @param propValue the value of property to be put
     * @return <code>null</code> if the property is no exist and
     *         put is succeeded, the existing value of the property otherwise
     */
    String putPropertyIfAbsent(String propName, String propValue);

    /**
     * Removes the property associated with given property name.
     *
     * @param propName the name of property to be removed
     * @return the old value of the property if it is exist and
     *         remove is succeeded, <code>null</code> otherwise
     */
    String removeProperty(String propName);

}
