package com.runsidekick.agent.tracepoint.domain;

/**
 * @author serkan
 */
public class Frame {

    private final String className;
    private final String methodName;
    private final int lineNo;
    private final Variables variables;

    public Frame(String className, String methodName, int lineNo) {
        this.className = className;
        this.methodName = methodName;
        this.lineNo = lineNo;
        this.variables = null;
    }

    public Frame(StackTraceElement stackTraceElement) {
        this.className = stackTraceElement.getClassName();
        this.methodName = stackTraceElement.getMethodName();
        this.lineNo = stackTraceElement.getLineNumber();
        this.variables = null;
    }

    public Frame(String className, String methodName, int lineNo, Variables variables) {
        this.className = className;
        this.methodName = methodName;
        this.lineNo = lineNo;
        this.variables = variables;
    }

    public Frame(StackTraceElement stackTraceElement, Variables variables) {
        this.className = stackTraceElement.getClassName();
        this.methodName = stackTraceElement.getMethodName();
        this.lineNo = stackTraceElement.getLineNumber();
        this.variables = variables;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getLineNo() {
        return lineNo;
    }

    public Variables getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", lineNo=" + lineNo +
                ", variables=" + variables +
                '}';
    }

}
