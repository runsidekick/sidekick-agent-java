package com.runsidekick.agent.tracepoint.request;

import com.runsidekick.agent.broker.request.impl.BaseRequest;

import java.util.List;

/**
 * @author oguzhan
 */
public class RemoveBatchTracePointRequest extends BaseRequest {

    private List<String> tracePointIds;

    public List<String> getTracePointIds() {
        return tracePointIds;
    }

    public void setTracePointIds(List<String> tracePointIds) {
        this.tracePointIds = tracePointIds;
    }

    @Override
    public String toString() {
        return "RemoveTracePointRequest{" +
                "tracePointIds='" + tracePointIds + '\'' +
                ", client='" + client + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
