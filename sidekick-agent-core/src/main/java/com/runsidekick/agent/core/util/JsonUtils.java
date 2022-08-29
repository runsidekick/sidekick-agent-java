package com.runsidekick.agent.core.util;

import org.json.JSONObject;

import java.util.Map;

/**
 * Utility class for JSON related stuff.
 *
 * @author serkan
 */
public final class JsonUtils {

    private JsonUtils() {
    }

    public static Map jsonToMap(String json) {
        return new JSONObject(json).toMap();
    }

}
