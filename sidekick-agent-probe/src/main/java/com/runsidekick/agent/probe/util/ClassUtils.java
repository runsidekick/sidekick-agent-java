package com.runsidekick.agent.probe.util;

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

    private ClassUtils() {
    }

    public static String extractClassName(String fileName) {
        fileName = normalizeFileName(fileName);
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

}
