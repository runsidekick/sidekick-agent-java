package com.runsidekick.agent.instrument;

import java.lang.instrument.Instrumentation;

/**
 * Main class of dynamically created java agent class to be invoked and
 * passed {@link Instrumentation} instance.
 *
 * @author serkan
 */
public class Installer {

    public static volatile Instrumentation INSTRUMENTATION;

    public static void agentmain(String agentArgs, Instrumentation inst) {
        INSTRUMENTATION = inst;
    }

    public static void premain(String arguments, Instrumentation inst) {
        INSTRUMENTATION = inst;
    }

}
