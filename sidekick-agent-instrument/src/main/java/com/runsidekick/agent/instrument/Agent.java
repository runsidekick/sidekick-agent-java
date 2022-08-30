package com.runsidekick.agent.instrument;

import com.runsidekick.agent.core.initialize.EnvironmentInitializerManager;
import com.runsidekick.agent.core.instance.InstanceDiscovery;
import com.runsidekick.agent.core.util.ExceptionUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * Main class of java agent class to be invoked and passed {@link Instrumentation} instance.
 *
 * @author serkan
 */
public final class Agent {

    private Agent() {
    }

    public static void agentmain(String arguments, Instrumentation instrumentation) {
        handleArguments(arguments, instrumentation);
        InstrumentSupport.activate(instrumentation, arguments);
        EnvironmentInitializerManager.ensureInitialized();
        for (AgentAware agentAware : InstanceDiscovery.instancesOf(AgentAware.class)) {
            agentAware.onAgentStart(arguments, instrumentation);
        }
    }

    public static void premain(String arguments, Instrumentation instrumentation) {
        handleArguments(arguments, instrumentation);
        InstrumentSupport.activate(instrumentation, arguments);
        EnvironmentInitializerManager.ensureInitialized();
        for (AgentAware agentAware : InstanceDiscovery.instancesOf(AgentAware.class)) {
            agentAware.onAgentStart(arguments, instrumentation);
        }
    }

    private static Map<String, String> parseArguments(String arguments) {
        if (arguments == null) {
            return null;
        }
        String[] argParts = arguments.split("\\s*,\\s*");
        Map<String, String> argMap = new HashMap<String, String>(argParts.length);
        for (String argPart : argParts) {
            argPart = argPart.trim();
            if (argPart.length() > 0) {
                String[] splittedArgPart = argPart.split("\\s*=\\s*");
                if (splittedArgPart.length != 2) {
                    throw new IllegalArgumentException(
                            "Agent arguments must be in 'key=value' format " +
                            "by separating each argument with comma (',')");
                }
                String argName = splittedArgPart[0];
                String argValue = splittedArgPart[1];
                argMap.put(argName, argValue);
            }
        }
        return argMap;
    }

    private static void handleArguments(String arguments, Instrumentation instrumentation) {
        Map<String, String> argMap = parseArguments(arguments);
        if (argMap == null) {
            return;
        }
        String argValue;
        argValue = argMap.get("jarsToLoad");
        if (argValue != null) {
            handleJarsToLoad(argValue, instrumentation);
        }
        argValue = argMap.get("propFilesToLoad");
        if (argValue != null) {
            handlePropFilesToLoad(argValue);
        }
    }

    private static void handleJarsToLoad(String argValue, Instrumentation instrumentation) {
        String[] splittedJarsToLoad = argValue.split("\\s*;\\s*");
        for (String jarToLoad : splittedJarsToLoad) {
            jarToLoad = jarToLoad.trim();
            if (jarToLoad.length() > 0) {
                System.out.println("[SIDEKICK] Loading jar " + jarToLoad + " ...");
                try {
                    instrumentation.appendToSystemClassLoaderSearch(new JarFile(jarToLoad));
                } catch (IOException e) {
                    ExceptionUtils.sneakyThrow(e);
                }
            }
        }
    }

    private static void handlePropFilesToLoad(String argValue) {
        String[] splittedPropFilesToLoad = argValue.split("\\s*;\\s*");
        for (String propFileToLoad : splittedPropFilesToLoad) {
            propFileToLoad = propFileToLoad.trim();
            if (propFileToLoad.length() > 0) {
                System.out.println("[SIDEKICK] Loading properties from " + propFileToLoad + " ...");
                Properties properties = new Properties();
                try {
                    properties.load(new FileInputStream(propFileToLoad));
                    for (String propName : properties.stringPropertyNames()) {
                        String propValue = properties.getProperty(propName);
                        System.out.println("[SIDEKICK] Loading property "+ propName + ": " + propValue + " ...");
                        System.setProperty(propName, propValue);
                    }
                } catch (IOException e) {
                    ExceptionUtils.sneakyThrow(e);
                }
            }
        }
    }

}
