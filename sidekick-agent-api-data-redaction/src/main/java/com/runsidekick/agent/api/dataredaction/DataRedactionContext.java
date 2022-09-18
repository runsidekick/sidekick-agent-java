package com.runsidekick.agent.api.dataredaction;

/**
 * @author yasin.kalafat
 */
public class DataRedactionContext {

    private final Class<?> clazz;
    private final String fileName;
    private final String className;
    private final int lineNo;
    private final String methodName;

    public DataRedactionContext(Class<?> clazz, String fileName, String className, int lineNo, String methodName) {
        this.clazz = clazz;
        this.className = className;
        this.fileName = fileName;
        this.lineNo = lineNo;
        this.methodName = methodName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getFileName() {
        return fileName;
    }

    public String getClassName() {
        return className;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getMethodName() {
        return methodName;
    }

}
