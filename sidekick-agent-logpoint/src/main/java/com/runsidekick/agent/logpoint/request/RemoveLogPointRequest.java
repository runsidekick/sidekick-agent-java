package com.runsidekick.agent.logpoint.request;

import com.runsidekick.agent.broker.request.impl.BaseRequest;

/**
 * @author yasin
 */
public class RemoveLogPointRequest extends BaseRequest {

    private String logPointId;
    private String fileName;
    private String className;
    private int lineNo;

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

    @Override
    public String toString() {
        return "RemoveLogPointRequest{" +
                "logPointId='" + logPointId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", lineNo=" + lineNo +
                ", id='" + id + '\'' +
                ", client='" + client + '\'' +
                '}';
    }

}
