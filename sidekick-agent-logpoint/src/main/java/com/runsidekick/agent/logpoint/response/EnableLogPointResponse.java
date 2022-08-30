package com.runsidekick.agent.logpoint.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author yasin
 */
public class EnableLogPointResponse extends BaseResponse<EnableLogPointResponse> {

    @Override
    public String toString() {
        return "EnableLogPointResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
