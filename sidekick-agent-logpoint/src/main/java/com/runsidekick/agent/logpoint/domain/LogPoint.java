package com.runsidekick.agent.logpoint.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author yasin
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogPoint {

    private String id;
    private String fileName;
    private String className;
    private int lineNo;
    private String client;
    private String logExpression;
    private boolean stdoutEnabled;
    private String logLevel;
    private String conditionExpression;
    private int expireSecs;
    private int expireCount;
    protected String fileHash;

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    private boolean disabled;

    public LogPoint() {
    }

    public LogPoint(String id, String fileName, String className, int lineNo, String client, String logExpression,
                    String conditionExpression, int expireSecs, int expireCount,
                    boolean disabled, boolean stdoutEnabled, String logLevel) {
        this.id = id;
        this.fileName = fileName;
        this.className = className;
        this.lineNo = lineNo;
        this.logExpression = logExpression;
        this.client = client;
        this.conditionExpression = conditionExpression;
        this.expireSecs = expireSecs;
        this.expireCount = expireCount;
        this.disabled = disabled;
        this.stdoutEnabled = stdoutEnabled;
        this.logLevel = logLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLogExpression() {
        return logExpression;
    }

    public void setLogExpression(String logExpression) {
        this.logExpression = logExpression;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public int getExpireSecs() {
        return expireSecs;
    }

    public void setExpireSecs(int expireSecs) {
        this.expireSecs = expireSecs;
    }

    public int getExpireCount() {
        return expireCount;
    }

    public void setExpireCount(int expireCount) {
        this.expireCount = expireCount;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isStdoutEnabled() {
        return stdoutEnabled;
    }

    public void setStdoutEnabled(boolean stdoutEnabled) {
        this.stdoutEnabled = stdoutEnabled;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public String toString() {
        return "LogPoint{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", lineNo=" + lineNo +
                ", client='" + client + '\'' +
                ", logExpression='" + logExpression + '\'' +
                ", conditionExpression='" + conditionExpression + '\'' +
                ", expireSecs=" + expireSecs +
                ", expireCount=" + expireCount +
                ", fileHash='" + fileHash + '\'' +
                ", stdoutEnabled=" + stdoutEnabled +
                ", logLevel='" + logLevel + '\'' +
                ", disabled=" + disabled +
                '}';
    }
}
