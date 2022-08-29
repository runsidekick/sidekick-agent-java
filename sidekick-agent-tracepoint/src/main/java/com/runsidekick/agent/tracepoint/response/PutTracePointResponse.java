package com.runsidekick.agent.tracepoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author serkan
 */
public class PutTracePointResponse extends BaseResponse<PutTracePointResponse> {

    @Override
    public String toString() {
        return "PutTracePointResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
