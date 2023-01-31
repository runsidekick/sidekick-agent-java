package com.runsidekick.agent.bootstrap;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Properties;

/**
 * Attaches java agent to target JVM process dynamically.
 *
 * @author serkan
 */
public class Attach {

    private static final String SIDEKICK_SYS_PROP_NAME_PREFIX = "sidekick.";
    private static final String SIDEKICK_ENV_VAR_NAME_PREFIX = "SIDEKICK_";

    private static final String SIDEKICK_OPTION_SEPARATOR = ";";
    private static final String SIDEKICK_OPTION_NAME_VALUE_SEPARATOR = "=";

    private static final String TARGET_PID_SYS_PROP_NAME = "sidekick.agent.target.pid";
    private static final String TARGET_PID_ENV_VAR_NAME = "SIDEKICK_AGENT_TARGET_PID";

    public static void main(String[] args) throws Exception {
        String targetProcessId = getTargetProcessId();
        if (targetProcessId == null) {
            throw new IllegalArgumentException("Target process id needs to be specified to attach");
        }

        String options = getOptions();

        File agentFile = Agent.exportAndGetAgentFile();

        attachAgent(agentFile, targetProcessId, options);
    }

    private static String getTargetProcessId() {
        String targetProcessId = System.getProperty(TARGET_PID_SYS_PROP_NAME);
        if (targetProcessId == null) {
            targetProcessId = System.getenv(TARGET_PID_ENV_VAR_NAME);
        }
        return targetProcessId;
    }

    private static String envVarToPropName(String envVarName) {
        return envVarName.toLowerCase().replace("_", ".");
    }

    private static String getOptions() {
        StringBuilder optionsBuilder = new StringBuilder();

        for (Map.Entry<String, String> e : System.getenv().entrySet()) {
            String envVarName = e.getKey();
            String envVarValue = e.getValue();
            if (envVarName.startsWith(SIDEKICK_ENV_VAR_NAME_PREFIX)) {
                String optionName = envVarToPropName(envVarName);
                if (optionsBuilder.length() > 0) {
                    optionsBuilder.append(SIDEKICK_OPTION_SEPARATOR);
                }
                optionsBuilder.append(optionName).append(SIDEKICK_OPTION_NAME_VALUE_SEPARATOR).append(envVarValue);
            }
        }

        Properties sysProps = System.getProperties();
        for (String propName : sysProps.stringPropertyNames()) {
            if (propName.startsWith(SIDEKICK_SYS_PROP_NAME_PREFIX)) {
                String propValue = sysProps.getProperty(propName);
                if (optionsBuilder.length() > 0) {
                    optionsBuilder.append(SIDEKICK_OPTION_SEPARATOR);
                }
                optionsBuilder.append(propName).append(SIDEKICK_OPTION_NAME_VALUE_SEPARATOR).append(propValue);
            }
        }

        return optionsBuilder.toString();
    }

    private static void attachAgent(File agentFile, String procId, String options) throws Exception {
        Class vmClass = null;
        String name = "com.sun.tools.attach.VirtualMachine";
        try {
            vmClass = Class.forName(name);
        } catch (Exception e) {
            String javaHome = System.getProperty("java.home");
            String toolsPath = javaHome.replace('\\', '/') + "/../lib/tools.jar";
            File toolsJarFile = new File(toolsPath);
            if (!toolsJarFile.exists()) {
                throw new IllegalStateException(
                        "Unable to find 'tools.jar'. Please be sure 'tools.jar' is available in the Java home");
            }
            URL url = toolsJarFile.toURI().toURL();
            ClassLoader classLoader = new URLClassLoader(new URL[]{url}, null);
            try {
                vmClass = classLoader.loadClass(name);
            } catch (Exception ex) {
                throw new IllegalStateException(
                        String.format(
                                "Unable to load '%s' from 'tools.jar' at '%s'",
                                name, toolsJarFile.getAbsolutePath()));
            }
        }
        if (vmClass == null) {
            throw new IllegalStateException("Unable to find VM class");
        }
        Object vm = vmClass.getDeclaredMethod("attach", String.class).invoke(null, procId);
        Method loadAgentMethod = vmClass.getDeclaredMethod("loadAgent", String.class, String.class);
        loadAgentMethod.invoke(vm, agentFile.getAbsolutePath(), options);
    }

}
