package com.runsidekick.agent.jdk.attach;

import java.io.File;

/**
 * Support class for providing JDK's Attach API related stuff.
 *
 * @author serkan
 */
public final class JDKAttachSupport {

    private JDKAttachSupport() {
    }

    /**
     * Checks whether JDK's Attach API related classes are provided
     * by JDK (if it is JDK instead of JRE).
     *
     * @return <code>true</code> if Attach API related classes are provided
     *         <code>false</code> otherwise
     */
    public static boolean areAttachAPIRelatedClassesProvidedByJDK() {
        String name = "com.sun.tools.attach.VirtualMachine";
        try {
            Class.forName(name);
            return true;
        } catch (Exception e) {
            String toolsPath = System.getProperty("java.home").replace('\\', '/') + "/../lib/tools.jar";
            return new File(toolsPath).exists();
        }
    }

}
