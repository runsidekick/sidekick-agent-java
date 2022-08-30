package com.runsidekick.agent.tracepoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author serkan
 */
public class DisableTracePointResponse extends BaseResponse<DisableTracePointResponse> {

    @Override
    public String toString() {
        return "DisableTracePointResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
