package com.runsidekick.agent.core.logger;

import com.runsidekick.agent.core.util.ExceptionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Standard logger implementation which prints to <code>stdout</code> or <code>stderr</code>.
 *
 * @author serkan
 */
public final class StdLogger {

    private static final String SIDEKICK_PREFIX = "[SIDEKICK] ";
    private static final String DEBUG_LEVEL = "DEBUG ";
    private static final String INFO_LEVEL = "INFO  ";
    private static final String ERROR_LEVEL = "ERROR ";
    private static final String DEBUG_ENABLE_CONFIG_NAME = "sidekick.agent.debug.enable";
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static final boolean DEBUG_ENABLE = isDebugEnabled();

    private StdLogger() {
    }

    private static boolean isDebugEnabled() {
        for (String propName : System.getProperties().stringPropertyNames()) {
            String sysPropName = propName.trim();
            String sysPropValue = System.getProperty(propName);
            if (DEBUG_ENABLE_CONFIG_NAME.equalsIgnoreCase(sysPropName)) {
                return Boolean.parseBoolean(sysPropValue);
            }
        }

        for (Map.Entry<String, String> e : System.getenv().entrySet()) {
            String envVarName = e.getKey().trim();
            String envVarValue = e.getValue().trim();
            if (DEBUG_ENABLE_CONFIG_NAME.equalsIgnoreCase(envVarName.replace("_", "."))) {
                return Boolean.parseBoolean(envVarValue);
            }
        }

        return false;
    }

    private static String getTime() {
        return TIME_FORMAT.format(new Date());
    }

    private static String getLogPrefix(String level) {
        return SIDEKICK_PREFIX + level + getTime() + " [" + Thread.currentThread().getName() + "] " + ": ";
    }

    public static void debug(String message) {
        if (DEBUG_ENABLE) {
            System.out.println(getLogPrefix(DEBUG_LEVEL) + message);
        }
    }

    public static void info(String message) {
        System.out.println(getLogPrefix(INFO_LEVEL) + message);
    }

    public static void error(String message) {
        System.err.println(getLogPrefix(ERROR_LEVEL) + message);
    }

    public static void error(String message, Throwable error) {
        System.err.println(getLogPrefix(ERROR_LEVEL) + message);
        System.err.println(ExceptionUtils.toString(error));
    }

}
