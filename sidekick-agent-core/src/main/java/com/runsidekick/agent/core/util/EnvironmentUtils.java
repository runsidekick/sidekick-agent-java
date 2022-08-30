package com.runsidekick.agent.core.util;

import com.runsidekick.agent.core.logger.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for providing environment related stuff.
 *
 * @author serkan
 */
public final class EnvironmentUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentUtils.class);

    private static final String APP_PROP_FILE_NAME = "sidekick.properties";

    public static final String AGENT_VERSION = getAgentVersion();
    public static final String JVM_VERSION = getJVMVersion();

    private static String getJVMVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2);
        }
        // Allow these formats:
        // 1.8.0_72-ea
        // 9-ea
        // 9
        // 9.0.1
        int dotPos = version.indexOf('.');
        int dashPos = version.indexOf('-');
        return version.substring(0, dotPos > -1 ? dotPos : dashPos > -1 ? dashPos : 1);
    }

    private static String getAgentVersion() {
        InputStream is =
                EnvironmentUtils.class.
                        getClassLoader().getResourceAsStream(APP_PROP_FILE_NAME);
        if (is == null) {
            LOGGER.warn(String.format(
                    "'%s' couldn't be found in the classpath. " +
                    "So no version is found ...", APP_PROP_FILE_NAME));
            return "N/A";
        }
        Properties props = new Properties();
        try {
            props.load(is);
            is.close();
            return (String) props.get("version");
        } catch (IOException e) {
            LOGGER.warn(String.format(
                    "'%s' couldn't be read. " +
                    "So no version is found ...", APP_PROP_FILE_NAME));
            return "N/A";
        }
    }

    private EnvironmentUtils() {
    }

}
