package com.runsidekick.agent.logpoint.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

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
    private String fileHash;
    private boolean disabled;
    private boolean predefined;
    private Set<String> tags;

    public LogPoint() {
    }

    public LogPoint(String id, String fileName, String className, int lineNo, String client, String logExpression,
                    String conditionExpression, int expireSecs, int expireCount,
                    boolean disabled, boolean stdoutEnabled, String logLevel, boolean predefined, Set<String> tags) {
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
        this.predefined = predefined;
        this.tags = tags;
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

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
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

    public boolean isPredefined() {
        return predefined;
    }

    public void setPredefined(boolean predefined) {
        this.predefined = predefined;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
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
                ", predefined=" + predefined +
                ", tags='" + tags + '\'' +
                '}';
    }
}
