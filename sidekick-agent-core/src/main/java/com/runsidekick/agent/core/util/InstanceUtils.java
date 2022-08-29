package com.runsidekick.agent.core.util;

import com.runsidekick.agent.core.factory.Factory;
import com.runsidekick.agent.core.instance.InstanceProvider;
import com.runsidekick.agent.core.instance.InstanceScope;

/**
 * Utility class for providing instance/object related stuff.
 *
 * @author serkan
 */
public final class InstanceUtils {

    private InstanceUtils() {
    }

    public static <T> T getInstanceFromProperties(String instanceFactoryPropName,
                                                  String instancePropName,
                                                  InstanceScope scope) {
        return getInstanceFromProperties(instanceFactoryPropName, instancePropName, scope, scope);
    }

    public static <T> T getInstanceFromProperties(String instanceFactoryPropName,
                                                  String instancePropName,
                                                  InstanceScope instanceFactoryScope,
                                                  InstanceScope instanceScope) {
        String instanceFactoryClassName =
                instanceFactoryPropName != null
                        ? PropertyUtils.getStringProperty(instanceFactoryPropName)
                        : null;
        Factory<T> instanceFactory;
        Class<Factory<T>> instanceFactoryClass = null;
        if (instanceFactoryClassName != null) {
            try {
                instanceFactoryClass = ClassUtils.getClassWithException(instanceFactoryClassName);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (instanceFactoryClass != null) {
            instanceFactory = InstanceProvider.getInstance(instanceFactoryClass, instanceFactoryScope);
            return instanceFactory.create();
        } else {
            String instanceClassName = PropertyUtils.getStringProperty(instancePropName);
            if (instanceClassName != null) {
                try {
                    Class<T> instanceClass = ClassUtils.getClassWithException(instanceClassName);
                    return InstanceProvider.getInstance(instanceClass, instanceScope);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return null;
    }

    public static <T> T getInstanceFromProperties(String instanceFactoryPropName,
                                                  String instancePropName,
                                                  InstanceScope scope,
                                                  T defaultInstance) {
        return getInstanceFromProperties(instanceFactoryPropName, instancePropName,
                                         scope, scope, defaultInstance);
    }

    public static <T> T getInstanceFromProperties(String instanceFactoryPropName,
                                                  String instancePropName,
                                                  InstanceScope instanceFactoryScope,
                                                  InstanceScope instanceScope,
                                                  T defaultInstance) {
        T instance =
                getInstanceFromProperties(
                        instanceFactoryPropName,
                        instancePropName,
                        instanceFactoryScope,
                        instanceScope);
        if (instance != null) {
            return instance;
        } else {
            return defaultInstance;
        }
    }

}
