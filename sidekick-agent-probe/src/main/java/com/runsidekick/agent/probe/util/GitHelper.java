package com.runsidekick.agent.probe.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author yasin.kalafat
 */
public final class GitHelper {

    private static final List<Pattern> FILE_NAME_URL_PATTERNS =
            Arrays.asList(
                    Pattern.compile(".*api\\.github\\.com\\/repos\\/[^\\/]+\\/[^\\/]+\\/contents/(?<fileName>[^\\?]+).*"),
                    Pattern.compile("gitlab\\.com\\/repos\\/[^\\/]+\\/[^\\/]+\\/contents/(?<fileName>[^\\?]+).*")
            );
    private static final Map<String, String> REPO_API_URL_MAPPING = new HashMap<String, String>() {{
        put("github.com", "api.github.com/repos");
        put("gitlab.com", "gitlab.com/repos");
    }};

    public static String normalizeFileName(String fileNameURL) {
        String fileName = fileNameURL;
        for (Pattern pattern : FILE_NAME_URL_PATTERNS) {
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.matches()) {
                fileName = matcher.group("fileName");
                break;
            }
        }
        return fileName;
    }
}
