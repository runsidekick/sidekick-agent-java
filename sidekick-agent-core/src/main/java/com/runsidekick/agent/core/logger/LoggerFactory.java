package com.runsidekick.agent.core.logger;

import com.runsidekick.agent.core.util.StringUtils;
import org.apache.commons.io.output.NullPrintStream;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to provide {@link Logger}s
 *
 * @author serkan
 */
public final class LoggerFactory {

    private static final Level DEFAULT_LOG_LEVEL = Level.ERROR;
    private static final String LOG_LEVEL_SYS_PROP_NAME = "sidekick.agent.log.level";
    private static final String LOG_LEVEL_ENV_VAR_NAME = "SIDEKICK_AGENT_LOG_LEVEL";
    private static final String DEBUG_ENABLE_SYS_PROP_NAME = "sidekick.agent.debug.enable";
    private static final String DEBUG_ENABLE_ENV_VAR_NAME = "SIDEKICK_AGENT_DEBUG_ENABLE";
    private static final Map<Level, java.util.logging.Level> JAVA_LOG_LEVEL_MAPPING =
            new HashMap<Level, java.util.logging.Level>() { {
                    put(Level.TRACE, java.util.logging.Level.FINEST);
                    put(Level.DEBUG, java.util.logging.Level.FINE);
                    put(Level.INFO, java.util.logging.Level.INFO);
                    put(Level.WARNING, java.util.logging.Level.WARNING);
                    put(Level.ERROR, java.util.logging.Level.SEVERE);
                    put(Level.OFF, java.util.logging.Level.OFF);
                }};

    private static final ILoggerFactory loggerFactory;
    /**
     * Keep strong reference to the logger to prevent it to be collected by GC
     * as {@link java.util.logging.LogManager} keeps weak ref to the loggers.
     * <p>If it is collected when it is only weakly reachable,
     * its new instance will not have the configured log level here.</p>
     */
    private static java.util.logging.Logger javaLogger;

    static {
        // Hacky way to disable logs during SLF4J initialization
        PrintStream stdOut = System.out;
        PrintStream stdErr = System.err;
        try {
            System.setOut(new NullPrintStream());
            System.setErr(new NullPrintStream());
            loggerFactory = org.slf4j.LoggerFactory.getILoggerFactory();
        } finally {
            System.setOut(stdOut);
            System.setErr(stdErr);
        }

        Level level = DEFAULT_LOG_LEVEL;
        String levelStr = getLogLevel();
        try {
            level = Level.valueOf(StringUtils.toUpperCase(levelStr));
        } catch (IllegalArgumentException e) {
            StdLogger.error(String.format(
                    "Invalid log level: %s. Continuing with the default level: %s",
                    levelStr, DEFAULT_LOG_LEVEL.name()));
        }
        Configurator.currentConfig().level(level).activate();

        javaLogger = java.util.logging.Logger.getLogger("com.runsidekick.agent");
        javaLogger.setLevel(JAVA_LOG_LEVEL_MAPPING.getOrDefault(level, java.util.logging.Level.OFF));
    }

    private LoggerFactory() {
    }

    private static String getLogLevel() {
        String debugEnable = null;

        for (String propName : System.getProperties().stringPropertyNames()) {
            String sysPropName = propName.trim();
            if (LOG_LEVEL_SYS_PROP_NAME.equalsIgnoreCase(sysPropName)) {
                return System.getProperty(propName).trim();
            }
            if (debugEnable == null && DEBUG_ENABLE_SYS_PROP_NAME.equalsIgnoreCase(sysPropName)) {
                debugEnable = System.getProperty(propName).trim();
            }
        }

        for (Map.Entry<String, String> e : System.getenv().entrySet()) {
            String envVarName = e.getKey().trim();
            String envVarValue = e.getValue().trim();
            if (LOG_LEVEL_ENV_VAR_NAME.equalsIgnoreCase(envVarName)) {
                return envVarValue;
            }
            if (debugEnable == null && DEBUG_ENABLE_ENV_VAR_NAME.equalsIgnoreCase(envVarName)) {
                debugEnable = envVarValue;
            }
        }

        if (Boolean.parseBoolean(debugEnable)) {
            return Level.DEBUG.name();
        }

        return DEFAULT_LOG_LEVEL.name();
    }

    /**
     * Return a {@link Logger logger} named according to the name parameter using the
     * statically bound {@link ILoggerFactory} instance.
     *
     * @param name the name of the {@link Logger logger}.
     * @return the {@link Logger logger}
     */
    public static Logger getLogger(String name) {
        return loggerFactory.getLogger(name);
    }

    /**
     * Return a {@link Logger logger} named corresponding to the class passed as parameter,
     * using the statically bound {@link ILoggerFactory} instance.
     *
     * @param clazz the returned {@link Logger logger} will be named after clazz
     * @return the {@link Logger logger}
     */
    public static Logger getLogger(Class<?> clazz) {
        return loggerFactory.getLogger(clazz.getName());
    }

}
