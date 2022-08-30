package com.runsidekick.agent.tracepoint.response;

import com.runsidekick.agent.tracepoint.domain.TracePoint;
import com.runsidekick.agent.broker.response.impl.BaseResponse;

import java.util.List;

/**
 * @author serkan
 */
public class FilterTracePointsResponse extends BaseResponse<FilterTracePointsResponse> {

    protected List<TracePoint> tracePoints;

    public FilterTracePointsResponse() {
    }

    public List<TracePoint> getTracePoints() {
        return tracePoints;
    }

    public void setTracePoints(List<TracePoint> tracePoints) {
        this.tracePoints = tracePoints;
    }

    @Override
    public String toString() {
        return "FilterTracePointsResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", tracePoints=" + tracePoints +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
