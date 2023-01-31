package com.runsidekick.agent.instrument;

import com.runsidekick.agent.core.initialize.EnvironmentInitializerManager;
import com.runsidekick.agent.core.instance.InstanceDiscovery;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;

/**
 * Main class of java agent class to be invoked and passed {@link Instrumentation} instance.
 *
 * @author serkan
 */
public final class Agent {

    private Agent() {
    }

    public static void agentmain(String arguments, Instrumentation instrumentation) {
        handleArguments(arguments);

        InstrumentSupport.activate(instrumentation, arguments);

        EnvironmentInitializerManager.ensureInitialized();

        for (AgentAware agentAware : InstanceDiscovery.instancesOf(AgentAware.class)) {
            agentAware.onAgentStart(arguments, instrumentation);
        }
    }

    public static void premain(String arguments, Instrumentation instrumentation) {
        handleArguments(arguments);

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
        String[] argParts = arguments.split("\\s*;\\s*");
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

    private static void handleArguments(String arguments) {
        Map<String, String> argMap = parseArguments(arguments);
        if (argMap == null) {
            return;
        }
        for (Map.Entry<String, String> e : argMap.entrySet()) {
            String argName = e.getKey();
            String argValue = e.getValue();
            System.setProperty(argName, argValue);
        }
    }

}
