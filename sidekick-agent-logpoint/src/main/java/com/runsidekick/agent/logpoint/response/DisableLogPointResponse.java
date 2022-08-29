package com.runsidekick.agent.logpoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author yasin
 */
public class DisableLogPointResponse extends BaseResponse<DisableLogPointResponse> {

    @Override
    public String toString() {
        return "DisableLogPointResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
