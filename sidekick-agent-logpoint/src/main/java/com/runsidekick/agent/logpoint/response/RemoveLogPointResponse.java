package com.runsidekick.agent.logpoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author yasin
 */
public class RemoveLogPointResponse extends BaseResponse<RemoveLogPointResponse> {

    @Override
    public String toString() {
        return "RemoveLogPointResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
