package com.runsidekick.agent.tracepoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author serkan
 */
public class RemoveTracePointResponse extends BaseResponse<RemoveTracePointResponse> {

    @Override
    public String toString() {
        return "RemoveTracePointResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
