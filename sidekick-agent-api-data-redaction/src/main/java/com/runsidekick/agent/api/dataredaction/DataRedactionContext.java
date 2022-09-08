package com.runsidekick.agent.api.dataredaction;

/**
 * @author yasin.kalafat
 */
public class DataRedactionContext {

    private Class<?> clazz;
    private String fileName;
    private String className;
    private int lineNo;
    private String methodName;
    private String logExpression;
    private String logMessage;

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

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getLogExpression() {
        return logExpression;
    }

    public void setLogExpression(String logExpression) {
        this.logExpression = logExpression;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
}
