package com.runsidekick.agent.probe.sourcecode;

import com.runsidekick.agent.probe.domain.ClassType;

/**
 * @author serkan
 */
public enum SourceCodeType {

    JAVA("java"),
    KOTLIN("kt"),
    SCALA("scala");

    private final String extension;

    SourceCodeType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static SourceCodeType fromExtension(String extension) {
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        for (SourceCodeType sourceCodeType : SourceCodeType.values()) {
            if (sourceCodeType.extension.equals(extension)) {
                return sourceCodeType;
            }
        }
        return null;
    }

    public static SourceCodeType fromFileName(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot < 0) {
            return null;
        }
        String extension = fileName.substring(lastDot + 1);
        return fromExtension(extension);
    }

    public static SourceCodeType fromClassType(ClassType classType) {
        return fromExtension(classType.getExtension());
    }

}
