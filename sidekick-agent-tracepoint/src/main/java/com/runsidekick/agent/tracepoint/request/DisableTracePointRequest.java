package com.runsidekick.agent.tracepoint.request;

import com.runsidekick.agent.broker.request.impl.BaseRequest;

/**
 * @author serkan
 */
public class DisableTracePointRequest extends BaseRequest {

    private String tracePointId;
    private String fileName;
    private String className;
    private int lineNo;

    public String getTracePointId() {
        return tracePointId;
    }

    public void setTracePointId(String tracePointId) {
        this.tracePointId = tracePointId;
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
        return "DisableTracePointRequest{" +
                "tracePointId='" + tracePointId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", lineNo=" + lineNo +
                ", id='" + id + '\'' +
                ", client='" + client + '\'' +
                '}';
    }

}
