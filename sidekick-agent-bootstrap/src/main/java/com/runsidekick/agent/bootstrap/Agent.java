package com.runsidekick.agent.bootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Main class of java agent class to bootstrap real Java agent.
 *
 * @author serkan
 */
public final class Agent {

    private Agent() {
    }

    public static void agentmain(String arguments, Instrumentation instrumentation) {
        onMain(arguments, instrumentation);
    }

    public static void premain(String arguments, Instrumentation instrumentation) {
        onMain(arguments, instrumentation);
    }

    private static void onMain(String arguments, Instrumentation instrumentation) {
        Map<String, String> parsedArgs = parseArguments(arguments);
        exportAndStartAgent(Agent.class.getClassLoader(), arguments, instrumentation, parsedArgs);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static File exportAndGetAgentFile() throws IOException {
        URL agentLocation = Agent.class.getProtectionDomain().getCodeSource().getLocation();
        JarFile jarFile = new JarFile(agentLocation.getFile());
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        JarEntry agentJarEntry = null;
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            if (jarEntry.getName().startsWith("sidekick-agent-all")) {
                agentJarEntry = jarEntry;
                break;
            }
        }
        if (agentJarEntry == null) {
            throw new IllegalStateException("Couldn't find Sidekick agent to bootstrap!");
        }
        String name = agentJarEntry.getName();
        if (name.lastIndexOf('/') != -1) {
            name = name.substring(name.lastIndexOf('/') + 1);
        }
        File unpackFolder = getTempUnpackFolder(jarFile);
        File file = new File(unpackFolder, name);
        file.deleteOnExit();
        unpackFolder.deleteOnExit();
        if (!file.exists() || file.length() != agentJarEntry.getSize()) {
            unpack(jarFile, agentJarEntry, file);
        }
        return file;
    }

    private static void unpack(JarFile jarFile, JarEntry entry, File file) throws IOException {
        try (InputStream inputStream = jarFile.getInputStream(entry);
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[32 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
    }

    private static File getTempUnpackFolder(JarFile jarFile) {
        File tempFolder = new File(System.getProperty("java.io.tmpdir"));
        return createUnpackFolder(jarFile, tempFolder);
    }

    private static File createUnpackFolder(JarFile jarFile, File parent) {
        int attempts = 0;
        while (attempts++ < 1000) {
            String fileName = new File(jarFile.getName()).getName();
            File unpackFolder = new File(parent, fileName + "-sidekick-agent-" + UUID.randomUUID());
            if (unpackFolder.mkdirs()) {
                return unpackFolder;
            }
        }
        throw new IllegalStateException("Failed to create unpack folder in directory '" + parent + "'");
    }

    private static boolean exportAndStartAgent(ClassLoader classLoader,
                                               String arguments,
                                               Instrumentation instrumentation,
                                               Map<String, String> parsedArgs) {
        try {
            exportAgent(classLoader, instrumentation);
            return startAgent(classLoader, arguments, instrumentation);
        } catch (Throwable t) {
            System.err.println("[SIDEKICK] Couldn't load and start agent: " + t.getMessage());
        }
        return false;
    }

    private static void exportAgent(ClassLoader classLoader, Instrumentation instrumentation) throws Exception {
        File agentFile = exportAndGetAgentFile();
        instrumentation.appendToSystemClassLoaderSearch(new JarFile(agentFile));
    }

    private static boolean startAgent(ClassLoader classLoader,
                                      String arguments,
                                      Instrumentation instrumentation) {
        try {
            Class agentClass = classLoader.loadClass("com.runsidekick.agent.instrument.Agent");
            Method agentmainMethod = agentClass.getMethod("agentmain", String.class, Instrumentation.class);
            agentmainMethod.invoke(null, arguments, instrumentation);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (Throwable t) {
            System.err.println("[SIDEKICK] Couldn't start agent: " + t.getMessage());
            return false;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
