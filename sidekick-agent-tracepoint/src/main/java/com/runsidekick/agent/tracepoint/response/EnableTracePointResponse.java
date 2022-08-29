package com.runsidekick.agent.tracepoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author serkan
 */
public class EnableTracePointResponse extends BaseResponse<EnableTracePointResponse> {

    @Override
    public String toString() {
        return "EnableTracePointResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
