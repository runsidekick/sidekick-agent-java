package com.runsidekick.agent.core.property;

/**
 * Utility class for providing File related stuff.
 *
 * @author serkan
 */
final class FileUtils {

    private FileUtils() {
    }

    static String getProfiledFileName(String fileName, String profileName) {
        int extIds = fileName.lastIndexOf(".");
        if (extIds > 0) {
            return fileName.substring(0, extIds) + "-" + profileName + "." + fileName.substring(extIds + 1);
        } else {
            return fileName + "-" + profileName;
        }
    }

}
