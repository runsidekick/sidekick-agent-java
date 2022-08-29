package com.runsidekick.agent.tracepoint.trace;

/**
 * @author serkan
 */
public class TraceContext {

    private final String traceId;
    private final String transactionId;
    private final String spanId;

    public TraceContext(String traceId, String transactionId, String spanId) {
        this.traceId = traceId;
        this.transactionId = transactionId;
        this.spanId = spanId;
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

}
