package com.runsidekick.agent.core.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class for package related stuffs.
 *
 * @author serkan
 */
public final class PackageUtils {

    public PackageUtils() {
    }

    public static String extractPackageFromSource(String filepath) throws IOException {
        String packageName = "";
        BufferedReader buff = new BufferedReader(new FileReader(filepath));
        Throwable error = null;
        try {
            String currentLine = "";
            while((currentLine = buff.readLine()) != null && StringUtils.isEmpty(packageName)) {
                if (StringUtils.isNotEmpty(currentLine) && StringUtils.isEmpty(packageName)) {
                    packageName = currentLine.startsWith("package") ? resolvePackage(currentLine) : "";
                }
            }
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            if (buff != null) {
                if (error != null) {
                    try {
                        buff.close();
                    } catch (Throwable t) {
                        error.addSuppressed(t);
                    }
                } else {
                    buff.close();
                }
            }
        }
        return packageName;
    }

    private static String resolvePackage(String packageLine) {
        return packageLine.replace("package", "").replace(";", "").trim();
    }

}

