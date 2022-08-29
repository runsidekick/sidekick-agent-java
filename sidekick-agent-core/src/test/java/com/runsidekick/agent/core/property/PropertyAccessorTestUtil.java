package com.runsidekick.agent.core.property;

import com.runsidekick.agent.core.util.ClassUtils;
import com.runsidekick.agent.core.util.ExceptionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author serkan
 */
public class PropertyAccessorTestUtil {

    private static final Map<String, String> theUnmodifiableEnvironment;
    private static final Field envField;

    static {
        try {
            Class processEnvClass = ClassUtils.getClassWithException("java.lang.ProcessEnvironment");
            envField = processEnvClass.getDeclaredField("theUnmodifiableEnvironment");
            envField.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(envField, envField.getModifiers() & ~Modifier.FINAL);
            theUnmodifiableEnvironment = (Map<String, String>) envField.get(null);
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    private PropertyAccessorTestUtil() {
    }

    public static void setEnvironmentVariable(String key, String value) {
        try {
            Map<String, String> env = (Map<String, String>) envField.get(null);
            if (!(env instanceof ModifiableEnvironment)) {
                env = new ModifiableEnvironment();
                env.putAll(theUnmodifiableEnvironment);
                envField.set(null, env);
            }
            env.put(key, value);
        } catch (IllegalAccessException e) {
            ExceptionUtils.sneakyThrow(e);
        }
    }

    public static void resetEnvironmentVariables() {
        try {
            envField.set(null, theUnmodifiableEnvironment);
        } catch (IllegalAccessException e) {
            ExceptionUtils.sneakyThrow(e);
        }
    }

    private static class ModifiableEnvironment extends HashMap {
    }

}
