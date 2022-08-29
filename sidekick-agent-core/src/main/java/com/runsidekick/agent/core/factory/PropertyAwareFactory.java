package com.runsidekick.agent.core.factory;

import com.runsidekick.agent.core.util.ClassUtils;
import com.runsidekick.agent.core.instance.InstanceProvider;
import com.runsidekick.agent.core.instance.InstanceScope;
import com.runsidekick.agent.core.util.ExceptionUtils;
import com.runsidekick.agent.core.util.PropertyUtils;

import java.util.function.Supplier;

/**
 * {@link Factory} implementation which creates instances
 * according to given property which represents class name
 * of the instance to be created.
 *
 * @author serkan
 */
public class PropertyAwareFactory<T> implements Factory<T> {

    private final String propName;
    private final InstanceScope instanceScope;
    private final Supplier<T> defaultInstanceSupplier;

    public PropertyAwareFactory(String propName) {
        this(propName, InstanceScope.GLOBAL);
    }

    public PropertyAwareFactory(String propName, InstanceScope instanceScope) {
        this.propName = propName;
        this.instanceScope = instanceScope;
        this.defaultInstanceSupplier =
                new Supplier<T>() {
                    @Override
                    public T get() {
                        return null;
                    }
                };
    }

    public PropertyAwareFactory(String propName, T defaultInstance) {
        this(propName, InstanceScope.GLOBAL, defaultInstance);
    }

    public PropertyAwareFactory(String propName, InstanceScope instanceScope, T defaultInstance) {
        this.propName = propName;
        this.instanceScope = instanceScope;
        this.defaultInstanceSupplier =
                new Supplier<T>() {
                    @Override
                    public T get() {
                        return defaultInstance;
                    }
                };
    }

    public PropertyAwareFactory(String propName, Supplier<T> defaultInstanceSupplier) {
        this(propName, InstanceScope.GLOBAL, defaultInstanceSupplier);
    }

    public PropertyAwareFactory(String propName, InstanceScope instanceScope, Supplier<T> defaultInstanceSupplier) {
        this.propName = propName;
        this.instanceScope = instanceScope;
        this.defaultInstanceSupplier = defaultInstanceSupplier;
    }

    @Override
    public T create() {
        String className = PropertyUtils.getStringProperty(propName);
        if (className != null) {
            try {
                Class<T> clazz = ClassUtils.getClassWithException(propName);
                return InstanceProvider.getInstance(clazz, instanceScope);
            } catch (ClassNotFoundException e) {
                ExceptionUtils.sneakyThrow(e);
            }
        }
        return defaultInstanceSupplier.get();
    }

}
