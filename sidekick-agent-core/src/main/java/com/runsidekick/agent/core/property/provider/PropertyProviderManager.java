package com.runsidekick.agent.core.property.provider;

import com.runsidekick.agent.core.instance.InstanceDiscovery;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

/**
 * Manager class to manage {@link PropertyProvider} related operations.
 *
 * @author serkan
 */
public final class PropertyProviderManager {

    private static final ConcurrentMap<String, Set<PropertyProvider>> PROPERTY_PROVIDERS_MAP =
            new ConcurrentHashMap<String, Set<PropertyProvider>>();

    static {
        InstanceDiscovery.instancesOf(PropertyProvider.class).forEach(new Consumer<PropertyProvider>() {
            @Override
            public void accept(PropertyProvider propertyProvider) {
                registerProviderInternal(propertyProvider);
            }
        });
    }

    private PropertyProviderManager() {
    }

    private static void registerProviderInternal(PropertyProvider propertyProvider) {
        List<String> providedPropertyNames = propertyProvider.getProvidedPropertyNames();
        for (String propName : providedPropertyNames) {
            Set<PropertyProvider> propertyProviders = PROPERTY_PROVIDERS_MAP.get(propName);
            if (propertyProviders == null) {
                propertyProviders =
                        new ConcurrentSkipListSet<PropertyProvider>(
                                new Comparator<PropertyProvider>() {
                                    @Override
                                    public int compare(PropertyProvider o1, PropertyProvider o2) {
                                        return Integer.compare(o1.order(), o2.order());
                                    }
                                }
                        );
                Set<PropertyProvider> pps = PROPERTY_PROVIDERS_MAP.putIfAbsent(propName, propertyProviders);
                if (pps != null) {
                    propertyProviders = pps;
                }
            }
            propertyProviders.add(propertyProvider);
        }
    }

    public static void registerProvider(PropertyProvider propertyProvider) {
        registerProviderInternal(propertyProvider);
    }

    public static void deregisterProvider(PropertyProvider propertyProvider) {
        List<String> providedPropertyNames = propertyProvider.getProvidedPropertyNames();
        for (String propName : providedPropertyNames) {
            Set<PropertyProvider> propertyProviders = PROPERTY_PROVIDERS_MAP.get(propName);
            if (propertyProviders != null) {
                propertyProviders.remove(propertyProvider);
            }
        }
    }

    public static void removeProviders(String registeredPropName) {
        PROPERTY_PROVIDERS_MAP.remove(registeredPropName);
    }

    public static void clearProviders() {
        PROPERTY_PROVIDERS_MAP.clear();
    }

    public static String lookupProperty(String propName) {
        Set<PropertyProvider> propertyProviders = PROPERTY_PROVIDERS_MAP.get(propName);
        if (propertyProviders != null) {
            for (PropertyProvider propertyProvider : propertyProviders) {
                String propValue = propertyProvider.getProperty(propName);
                if (propValue != null) {
                    return propValue;
                }
            }
        }
        return null;
    }

    public static Map<String, String> getAllProvidedProperties() {
        Map<String, String> props = new HashMap<String, String>();
        for (Map.Entry<String, Set<PropertyProvider>> entry : PROPERTY_PROVIDERS_MAP.entrySet()) {
            String propName = entry.getKey();
            Set<PropertyProvider> propertyProviders = entry.getValue();
            for (PropertyProvider propertyProvider : propertyProviders) {
                String propValue = propertyProvider.getProperty(propName);
                if (propValue != null) {
                    props.put(propName, propValue);
                    break;
                }
            }
        }
        return props;
    }

}
