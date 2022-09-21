package com.runsidekick.agent.probe.util;

import com.runsidekick.agent.core.logger.LoggerFactory;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for class related stuff.
 *
 * @author serkan
 */
public class ClassUtils {

    private static final String SRC_MAIN_JAVA_DIRECTORY_NAME = "src/main/java/";
    private static final String SRC_MAIN_KOTLIN_DIRECTORY_NAME = "src/main/kotlin/";
    private static final String SRC_MAIN_SCALA_DIRECTORY_NAME = "src/main/scala/";
    private static final String SRC_DIRECTORY_NAME = "src/";
    private static final String[] SRC_DIRECTORIES = new String[] {
            SRC_MAIN_JAVA_DIRECTORY_NAME,
            SRC_MAIN_KOTLIN_DIRECTORY_NAME,
            SRC_MAIN_SCALA_DIRECTORY_NAME,
            SRC_DIRECTORY_NAME
    };
    private static final Map<String, String> REPO_API_URL_MAPPING = new HashMap<String, String>() {{
        put("github.com", "api.github.com/repos");
        put("gitlab.com", "api.gitlab.com/repos");
    }};
    private static final String CONTENTS = "/contents/";

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);

    private ClassUtils() {
    }

    public static String extractClassName(String fileName) {
        fileName = normalizeFileName(fileName);
        fileName = removeGitParts(fileName);
        for (String srcDirectory : SRC_DIRECTORIES) {
            int idx = fileName.indexOf(srcDirectory);
            if (idx >= 0) {
                fileName = fileName.substring(idx + srcDirectory.length());
                break;
            }
        }
        fileName = removeLeadingSlashes(fileName);
        fileName = removeFileExtension(fileName);
        return fileName.replace("/", ".");
    }

    private static String normalizeFileName(String fileName) {
        int idx = fileName.lastIndexOf("?");
        if (idx >= 0) {
            return fileName.substring(0, idx);
        } else {
            return fileName;
        }
    }

    private static String removeLeadingSlashes(String fileName) {
        return fileName.replaceFirst("^/+(?!$)", "");
    }

    private static String removeFileExtension(String fileName) {
        int idx = fileName.lastIndexOf(".");
        if (idx >= 0) {
            return fileName.substring(0, idx);
        } else {
            return fileName;
        }
    }

    public static String removeGitParts(String fileName) {
        for (Map.Entry<String, String> entry: REPO_API_URL_MAPPING.entrySet()) {
            if (fileName.contains(entry.getValue())) {
                fileName = fileName.substring(fileName.indexOf(CONTENTS) + CONTENTS.length());
                break;
            }
        }
        return fileName;
    }

}
