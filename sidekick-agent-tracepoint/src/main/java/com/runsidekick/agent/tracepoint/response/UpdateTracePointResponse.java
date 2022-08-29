package com.runsidekick.agent.tracepoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author serkan
 */
public class UpdateTracePointResponse extends BaseResponse<UpdateTracePointResponse> {

    @Override
    public String toString() {
        return "UpdateTracePointResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
