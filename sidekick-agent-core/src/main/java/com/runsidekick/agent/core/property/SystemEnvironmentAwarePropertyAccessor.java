package com.runsidekick.agent.core.property;

import com.runsidekick.agent.core.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Environment variable based {@link PropertyAccessor} implementation
 * which searches/loads properties from environment variables.
 *
 * @author serkan
 */
public class SystemEnvironmentAwarePropertyAccessor implements PropertyAccessor {

    private final Map<String, String> envVars = getLowerCaseEnvVars();

    private static Map<String, String> getLowerCaseEnvVars() {
        Map<String, String> envVars = new HashMap<>();
        for (Map.Entry<String, String> e : System.getenv().entrySet()) {
            String envVarName = e.getKey().trim();
            String envVarValue = e.getValue().trim();
            String normalizedEnvVarName = StringUtils.toLowerCase(envVarName).replace("_", ".");
            envVars.put(normalizedEnvVarName, envVarValue);
        }
        return envVars;
    }

    @Override
    public String getProperty(String propName) {
        return envVars.get(propName);
    }

    @Override
    public Map<String, String> getProperties() {
        return envVars;
    }

}
