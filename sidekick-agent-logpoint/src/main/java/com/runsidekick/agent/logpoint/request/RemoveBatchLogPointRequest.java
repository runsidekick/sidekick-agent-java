package com.runsidekick.agent.logpoint.request;

import com.runsidekick.agent.broker.request.impl.BaseRequest;

import java.util.List;

/**
 * @author yasin
 */
public class RemoveBatchLogPointRequest extends BaseRequest {

    private List<String> logPointIds;

    public List<String> getLogPointIds() {
        return logPointIds;
    }

    public void setLogPointIds(List<String> logPointIds) {
        this.logPointIds = logPointIds;
    }

    @Override
    public String toString() {
        return "RemoveLogPointRequest{" +
                "logPointIds='" + logPointIds + '\'' +
                ", client='" + client + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
