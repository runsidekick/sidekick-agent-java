package com.runsidekick.agent.probe.sourcecode;

import org.apache.commons.lang3.StringUtils;

/**
 * @author serkan
 */
public class SourceCode {

    private final String className;
    private final String sourceFileName;
    private final String sourceFilePath;
    private final SourceCodeType sourceCodeType;

    public SourceCode(String className, String sourceFileName,
                      String sourceFilePath, SourceCodeType sourceCodeType) {
        this.className = className;
        this.sourceFileName = sourceFileName;
        this.sourceFilePath = sourceFilePath;
        this.sourceCodeType = sourceCodeType;
    }

    public String getClassName() {
        return className;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public SourceCodeType getSourceCodeType() {
        return sourceCodeType;
    }

    public boolean hasSourceCodeFilePath() {
        return StringUtils.isNotEmpty(this.sourceFilePath);
    }

    @Override
    public String toString() {
        return "SourceCode{" +
                "className='" + className + '\'' +
                ", sourceFileName='" + sourceFileName + '\'' +
                ", sourceFilePath='" + sourceFilePath + '\'' +
                ", sourceCodeType=" + sourceCodeType +
                '}';
    }

}
