package com.runsidekick.agent.core.config;

import com.runsidekick.agent.core.util.PropertyUtils;
import com.runsidekick.agent.core.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yasin.kalafat
 */
public final class ConfigProvider {

    private static Map<String, Object> configMap = new ConcurrentHashMap<>();

    private static Map<String, String> configPropertyMapping = new HashMap<String, String>() {{
        put("sidekick.agent.tracepoint.stacktrace.maxdepth", "maxFrames");
        put("sidekick.agent.tracepoint.serialization.array.length.max", "maxProperties");
        put("sidekick.agent.tracepoint.serialization.depth.max", "maxParseDepth");
        put("-", "maxExpandFrames");
        put("errorCollectionEnable", "errorCollectionEnable");
        put("errorCollectionEnableCaptureFrame", "errorCollectionEnableCaptureFrame");
    }};

    private ConfigProvider() {

    }

    public static Integer getIntegerProperty(String propName, int defaultPropValue) {
        if (configPropertyMapping.containsKey(propName)) {
            String key = configPropertyMapping.get(propName);
            if (configMap.containsKey(key)) {
                return Integer.valueOf(configMap.get(key).toString());
            }
        }
        return defaultPropValue;
    }


    public static void setConfig(Map<String, Object> config) {
        configMap.clear();
        if (config != null && config.size() > 0) {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                configMap.put(entry.getKey(), entry.getValue());
            }
        }
    }
}