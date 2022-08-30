package com.runsidekick.agent.core.property;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.runsidekick.agent.core.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class for property access related stuff.
 *
 * @author serkan
 */
public final class PropertyAccessorUtil {

    private PropertyAccessorUtil() {
    }

    public static void loadProperties(Map<String, String> props, InputStream propertyFileStream,
                                      String propertyFileName) throws IOException {
        if (propertyFileStream != null) {
            if (propertyFileName.endsWith(".properties")) {
                Properties properties = new Properties();
                properties.load(propertyFileStream);
                for (String propName : properties.stringPropertyNames()) {
                    props.put(StringUtils.toLowerCase(propName), properties.getProperty(propName));
                }
            } else if (propertyFileName.endsWith(".yml") || propertyFileName.endsWith(".yaml")) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                Map<String, Object> properties = mapper.readValue(propertyFileStream, Map.class);
                putProperties(props, null, properties);
            } else {
                throw new IOException(
                        "Unrecognized file format. " +
                        "Only properties file (.properties) and YAML (.yml or .yaml) files are supported");
            }
        }
    }

    private static void putProperties(Map<String, String> destProps, String destPath, Map<String, Object> srcProps) {
        for (Map.Entry<String, Object> e : srcProps.entrySet()) {
            String propName = StringUtils.toLowerCase(e.getKey());
            Object propValue = e.getValue();
            String newDestPath = generateDestinationPath(destPath, propName);
            if (propValue instanceof Map) {
                putProperties(destProps, newDestPath, (Map<String, Object>) propValue);
            } else {
                destProps.put(newDestPath, propValue.toString());
            }
        }
    }

    private static String generateDestinationPath(String curPath, String propName) {
        if (StringUtils.isNullOrEmpty(curPath)) {
            return propName;
        } else {
            return curPath + "." + propName;
        }
    }

}
