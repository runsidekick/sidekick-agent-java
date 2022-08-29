package com.runsidekick.agent.tracepoint.event;

import com.runsidekick.agent.tracepoint.domain.Frame;
import com.runsidekick.agent.broker.event.impl.BaseEvent;

import java.util.List;

/**
 * @author serkan
 */
public class TracePointSnapshotEvent extends BaseEvent {

    private final String tracePointId;
    private final String fileName;
    private final String className;
    private final int lineNo;
    private final String methodName;
    private final List<Frame> frames;
    private final String traceId;
    private final String transactionId;
    private final String spanId;

    public TracePointSnapshotEvent(String tracePointId, String fileName, String className,
                                   int lineNo, String methodName, List<Frame> frames) {
        this.tracePointId = tracePointId;
        this.fileName = fileName;
        this.className = className;
        this.lineNo = lineNo;
        this.methodName = methodName;
        this.frames = frames;
        this.traceId = null;
        this.transactionId = null;
        this.spanId = null;
    }

    public TracePointSnapshotEvent(String tracePointId, String fileName, String className,
                                   int lineNo, String methodName, List<Frame> frames,
                                   String traceId, String transactionId, String spanId) {
        this.tracePointId = tracePointId;
        this.fileName = fileName;
        this.className = className;
        this.lineNo = lineNo;
        this.methodName = methodName;
        this.frames = frames;
        this.traceId = traceId;
        this.transactionId = transactionId;
        this.spanId = spanId;
    }

    public String getTracePointId() {
        return tracePointId;
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

    public List<Frame> getFrames() {
        return frames;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSpanId() {
        return spanId;
    }

    @Override
    public String toString() {
        return "TracePointSnapshotEvent{" +
                "tracePointId='" + tracePointId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", lineNo=" + lineNo +
                ", methodName='" + methodName + '\'' +
                ", frames=" + frames +
                ", traceId='" + traceId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", spanId='" + spanId + '\'' +
                ", id='" + id + '\'' +
                ", sendAck=" + sendAck +
                ", client='" + client + '\'' +
                ", time=" + time +
                ", hostName='" + hostName + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                '}';
    }

}
