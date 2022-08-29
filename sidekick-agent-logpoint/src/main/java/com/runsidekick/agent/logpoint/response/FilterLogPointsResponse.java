package com.runsidekick.agent.logpoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;
import com.runsidekick.agent.logpoint.domain.LogPoint;

import java.util.List;

/**
 * @author yasin
 */
public class FilterLogPointsResponse extends BaseResponse<FilterLogPointsResponse> {

    protected List<LogPoint> logPoints;

    public FilterLogPointsResponse() {
    }

    public List<LogPoint> getLogPoints() {
        return logPoints;
    }

    public void setLogPoints(List<LogPoint> logPoints) {
        this.logPoints = logPoints;
    }

    @Override
    public String toString() {
        return "FilterLogPointsResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", logPoints=" + logPoints +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
