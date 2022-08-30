package com.runsidekick.agent.core.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for class name related stuffs.
 *
 * @author serkan
 */
public final class ClassNameUtils {

    private ClassNameUtils() {
    }

    public static String getMostInnerClassName(String className) {
        if (StringUtils.isEmpty(className)) {
            return null;
        } else {
            int lastIndexOfDot = className.lastIndexOf(".");
            String classNameNoPackage = className.substring(lastIndexOfDot + 1);
            String[] classesName = classNameNoPackage.split("\\$");
            for(int i = classesName.length - 1; i >= 0; --i) {
                String classNameChunk = classesName[i];
                if (!StringUtils.isNumeric(classNameChunk)) {
                    return classNameChunk;
                }
            }
            return null;
        }
    }

    public static String extractClassNameFromSource(File file) throws IOException {
        return buildFqClassName(PackageUtils.extractPackageFromSource(file.getCanonicalPath()), file.getName());
    }

    private static String buildFqClassName(String packageName, String fileName) {
        return packageName.
                concat(StringUtils.isNotEmpty(packageName) ? "." : "").
                concat(FilenameUtils.removeExtension(fileName)).trim();
    }

    public static String extractBaseClassName(String className) {
        return className.contains("$") ? className.substring(0, className.indexOf("$")) : className;
    }

    public static String extractSourceFileName(String className, String extension) {
        String baseClassName = extractBaseClassName(className);
        String[] baseClassNameChunks = baseClassName.split("\\.");
        return baseClassNameChunks[baseClassNameChunks.length - 1].concat(".").concat(extension);
    }

    public static String extractSourceFilePath(String className, String extension) {
        String baseClassName = extractBaseClassName(className);
        return baseClassName.replace(".", "/").concat(".").concat(extension);
    }

    public static String extractClassFilePath(String className) {
        return className.replace(".", "/").concat(".class");
    }

}
