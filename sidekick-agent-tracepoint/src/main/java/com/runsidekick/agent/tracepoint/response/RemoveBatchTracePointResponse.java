package com.runsidekick.agent.tracepoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oguzhan
 */
public class RemoveBatchTracePointResponse extends BaseResponse<RemoveBatchTracePointResponse> {

    private List<String> removedTracePointIds;
    private Map<String, String> unRemovedTracePointIds;

    public RemoveBatchTracePointResponse() {
        this.removedTracePointIds = new ArrayList<>();
        this.unRemovedTracePointIds = new HashMap<>();
    }

    public List<String> getRemovedTracePointIds() {
        return removedTracePointIds;
    }

    public void setRemovedTracePointIds(List<String> removedTracePointIds) {
        this.removedTracePointIds = removedTracePointIds;
    }

    public Map<String, String> getUnRemovedTracePointIds() {
        return unRemovedTracePointIds;
    }

    public void setUnRemovedTracePointIds(Map<String, String> unRemovedTracePointIds) {
        this.unRemovedTracePointIds = unRemovedTracePointIds;
    }

    @Override
    public String toString() {
        return "RemoveTracePointResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", removedTracePointIds='" + removedTracePointIds + '\'' +
                ", unRemovedTracePointIds='" + unRemovedTracePointIds + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
