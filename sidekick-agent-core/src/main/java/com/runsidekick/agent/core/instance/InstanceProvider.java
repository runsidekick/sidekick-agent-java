package com.runsidekick.agent.core.instance;

import com.runsidekick.agent.core.util.ExceptionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Provides instance for given {@link Class} at given {@link InstanceScope}
 * by using specified {@link InstanceCreator}.
 *
 * @author serkan
 */
public final class InstanceProvider {

    private static final Map<InstanceScope, InstanceFactory> INSTANCE_FACTORY_MAP = new HashMap<>();
    private static final DefaultInstanceCreator DEFAULT_INSTANCE_CREATOR = new DefaultInstanceCreator();

    static {
        INSTANCE_FACTORY_MAP.put(
                InstanceScope.GLOBAL,
                new GlobalInstanceFactory());
        INSTANCE_FACTORY_MAP.put(
                InstanceScope.THREAD_LOCAL,
                new ThreadLocalInstanceFactory());
        INSTANCE_FACTORY_MAP.put(
                InstanceScope.INHERITABLE_THREAD_LOCAL,
                new InheritableThreadLocalInstanceFactory());
        INSTANCE_FACTORY_MAP.put(
                InstanceScope.PROTOTYPE,
                new PrototypeInstanceFactory());
    }

    private InstanceProvider() {
    }

    public static <T> T getInstance(Class<T> clazz, InstanceScope scope) {
        return getInstance(clazz, scope, DEFAULT_INSTANCE_CREATOR);
    }

    public static <T> T getInstance(Class<T> clazz, InstanceScope scope,
                                    InstanceCreator creator) {
        InstanceFactory instanceFactory = INSTANCE_FACTORY_MAP.get(scope);
        if (instanceFactory == null) {
            throw new IllegalArgumentException("Unsupported instance scope type: " + scope);
        }
        if (creator == null) {
            creator = DEFAULT_INSTANCE_CREATOR;
        }
        return instanceFactory.get(creator, clazz);
    }

    public static void clearScope(InstanceScope scope) {
        InstanceFactory instanceFactory = INSTANCE_FACTORY_MAP.get(scope);
        if (instanceFactory == null) {
            throw new IllegalArgumentException("Unsupported instance scope type: " + scope);
        }
        instanceFactory.clear();
    }

    private static class DefaultInstanceCreator implements InstanceCreator {

        @Override
        public <T> T create(Class<T> clazz) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                ExceptionUtils.sneakyThrow(e);
                return null;
            }
        }

    }

    private interface InstanceFactory {

        <T> T get(InstanceCreator creator, Class<T> clazz);
        void clear();

    }

    private static class GlobalInstanceFactory implements InstanceFactory {

        private final Map<Class, Object> instanceMap = Collections.synchronizedMap(new WeakHashMap());

        @Override
        public <T> T get(InstanceCreator creator, Class<T> clazz) {
            Object instance = instanceMap.get(clazz);
            if (instance == null) {
                synchronized (instanceMap) {
                    instance = instanceMap.get(clazz);
                    if (instance == null) {
                        instance = creator.create(clazz);
                        instanceMap.put(clazz, instance);
                    }
                }
            }
            return (T) instance;
        }

        @Override
        public void clear() {
            instanceMap.clear();
        }

    }

    private static class ThreadLocalInstanceFactory implements InstanceFactory {

        private final ThreadLocal<Object> threadLocalInstance = new ThreadLocal<>();

        @Override
        public <T> T get(InstanceCreator creator, Class<T> clazz) {
            Object instance = threadLocalInstance.get();
            if (instance == null) {
                instance = creator.create(clazz);
                threadLocalInstance.set(instance);
            }
            return (T) instance;
        }

        @Override
        public void clear() {
            threadLocalInstance.remove();
        }

    }

    private static class InheritableThreadLocalInstanceFactory implements InstanceFactory {

        private final ThreadLocal<Object> threadLocalInstance = new InheritableThreadLocal<>();

        @Override
        public <T> T get(InstanceCreator creator, Class<T> clazz) {
            Object instance = threadLocalInstance.get();
            if (instance == null) {
                instance = creator.create(clazz);
                threadLocalInstance.set(instance);
            }
            return (T) instance;
        }

        @Override
        public void clear() {
            threadLocalInstance.remove();
        }

    }

    private static class PrototypeInstanceFactory implements InstanceFactory {

        @Override
        public <T> T get(InstanceCreator creator, Class<T> clazz) {
            return creator.create(clazz);
        }

        @Override
        public void clear() {

        }

    }

    public static <T> T createLazyLoadableInstance(Class<T> instanceInterface, InstanceLoader<T> instanceLoader) {
        return createLazyLoadableInstance(instanceInterface, instanceLoader, null);
    }

    public static <T> T createLazyLoadableInstance(Class<T> instanceInterface,
                                                   InstanceLoader<T> instanceLoader,
                                                   Class<? extends T> instanceClass) {
        if (!instanceInterface.isInterface()) {
            throw new IllegalArgumentException("Specified type for instance interface is not an interface");
        }

        Method getInstTypeMethod = null;
        try {
            getInstTypeMethod = InstanceTypeAwareProxy.class.getMethod("getInstanceType");
        } catch (NoSuchMethodException e) {
            ExceptionUtils.sneakyThrow(e);
        }
        final Method getInstanceTypeMethod = getInstTypeMethod;

        Method getInstClassMethod = null;
        if (instanceClass != null) {
            try {
                getInstClassMethod = InstanceClassAwareProxy.class.getMethod("getInstanceClass");
            } catch (NoSuchMethodException e) {
                ExceptionUtils.sneakyThrow(e);
            }
        }
        final Method getInstanceClassMethod = getInstClassMethod;

        Method getInstMethod = null;
        try {
            getInstMethod = InstanceAwareProxy.class.getMethod("getInstance");
        } catch (NoSuchMethodException e) {
            ExceptionUtils.sneakyThrow(e);
        }
        final Method getInstanceMethod = getInstMethod;

        return (T) Proxy.newProxyInstance(
                instanceInterface.getClassLoader(),
                getInstanceClassMethod != null
                    ? new Class[] { InstanceTypeAwareProxy.class, InstanceClassAwareProxy.class, InstanceAwareProxy.class, instanceInterface }
                    : new Class[] { InstanceTypeAwareProxy.class, InstanceAwareProxy.class, instanceInterface },
                new InvocationHandler() {
                    private final Object mutex = new Object();
                    private volatile Object bean;
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.equals(getInstanceTypeMethod)) {
                            return instanceInterface;
                        } else if (getInstanceClassMethod != null && method.equals(getInstanceClassMethod)) {
                            return instanceClass;
                        } else if (method.equals(getInstanceMethod)) {
                            if (bean == null) {
                                synchronized (mutex) {
                                    if (bean == null) {
                                        bean = instanceLoader.load();
                                    }
                                }
                            }
                            return bean;
                        } else {
                            if (bean == null) {
                                synchronized (mutex) {
                                    if (bean == null) {
                                        bean = instanceLoader.load();
                                    }
                                }
                            }
                            try {
                                return method.invoke(bean, args);
                            } catch (InvocationTargetException e) {
                                throw ((InvocationTargetException) e).getTargetException();
                            } catch (UndeclaredThrowableException e) {
                                throw ((UndeclaredThrowableException) e).getUndeclaredThrowable();
                            }
                        }
                    }
                });
    }

}
