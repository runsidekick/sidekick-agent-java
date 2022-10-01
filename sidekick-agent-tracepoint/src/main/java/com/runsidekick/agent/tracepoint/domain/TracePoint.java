package com.runsidekick.agent.tracepoint.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

/**
 * @author serkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TracePoint {

    private String id;
    private String fileName;
    private String className;
    private int lineNo;
    private String client;
    private String conditionExpression;
    private int expireSecs;
    private int expireCount;
    private boolean tracingEnabled;
    private String fileHash;
    private boolean disabled;
    private boolean predefined;
    protected Set<String> tags;

    public TracePoint() {
    }

    public TracePoint(String id, String fileName, String className, int lineNo, String client,
                      String conditionExpression, int expireSecs, int expireCount,
                      boolean tracingEnabled, boolean disabled, boolean predefined, Set<String> tags) {
        this.id = id;
        this.fileName = fileName;
        this.className = className;
        this.lineNo = lineNo;
        this.client = client;
        this.conditionExpression = conditionExpression;
        this.expireSecs = expireSecs;
        this.expireCount = expireCount;
        this.tracingEnabled = tracingEnabled;
        this.disabled = disabled;
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

    public boolean isTracingEnabled() {
        return tracingEnabled;
    }

    public void setTracingEnabled(boolean tracingEnabled) {
        this.tracingEnabled = tracingEnabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
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
        return "TracePoint{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", lineNo=" + lineNo +
                ", client='" + client + '\'' +
                ", conditionExpression='" + conditionExpression + '\'' +
                ", expireSecs=" + expireSecs +
                ", expireCount=" + expireCount +
                ", tracingEnabled=" + tracingEnabled +
                ", disabled=" + disabled +
                ", predefined=" + predefined +
                ", tags='" + tags + '\'' +
                '}';
    }

}
