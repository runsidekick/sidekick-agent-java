package com.runsidekick.agent.logpoint.request;

import com.runsidekick.agent.broker.request.impl.BaseRequest;

import java.util.Set;

/**
 * @author yasin
 */
public class PutLogPointRequest extends BaseRequest {

    private String logPointId;
    private String fileName;
    private String className;
    private int lineNo;
    private String logExpression;
    private String fileHash;
    private String conditionExpression;
    private int expireSecs;
    private int expireCount;
    private boolean stdoutEnabled;
    private String logLevel;
    private boolean disable;
    private Set<String> tags;

    public String getLogPointId() {
        return logPointId;
    }

    public void setLogPointId(String logPointId) {
        this.logPointId = logPointId;
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

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
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

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "PutLogPointRequest{" +
                "logPointId='" + logPointId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", lineNo=" + lineNo +
                ", logExpression='" + logExpression + '\'' +
                ", fileHash='" + fileHash + '\'' +
                ", conditionExpression='" + conditionExpression + '\'' +
                ", expireSecs=" + expireSecs +
                ", expireCount=" + expireCount +
                ", id='" + id + '\'' +
                ", client='" + client + '\'' +
                ", stdoutEnabled=" + stdoutEnabled +
                ", logLevel='" + logLevel + '\'' +
                ", disable=" + disable +
                ", tags='" + tags +'\'' +
                '}';
    }
}
