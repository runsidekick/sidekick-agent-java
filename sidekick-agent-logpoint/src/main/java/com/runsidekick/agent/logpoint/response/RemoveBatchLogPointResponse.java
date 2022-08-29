package com.runsidekick.agent.logpoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yasin
 */
public class RemoveBatchLogPointResponse extends BaseResponse<RemoveBatchLogPointResponse> {

    private List<String> removedLogPointIds;
    private Map<String, String> unRemovedLogPointIds;

    public RemoveBatchLogPointResponse() {
        this.removedLogPointIds = new ArrayList<>();
        this.unRemovedLogPointIds = new HashMap<>();
    }

    public List<String> getRemovedLogPointIds() {
        return removedLogPointIds;
    }

    public void setRemovedLogPointIds(List<String> removedLogPointIds) {
        this.removedLogPointIds = removedLogPointIds;
    }

    public Map<String, String> getUnRemovedLogPointIds() {
        return unRemovedLogPointIds;
    }

    public void setUnRemovedLogPointIds(Map<String, String> unRemovedLogPointIds) {
        this.unRemovedLogPointIds = unRemovedLogPointIds;
    }

    @Override
    public String toString() {
        return "RemoveLogPointResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", removedLogPointIds='" + removedLogPointIds + '\'' +
                ", unRemovedLogPointIds='" + unRemovedLogPointIds + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
