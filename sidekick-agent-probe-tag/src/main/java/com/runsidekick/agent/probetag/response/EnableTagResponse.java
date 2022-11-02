package com.runsidekick.agent.probetag.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author yasin.kalafat
 */
public class EnableTagResponse extends BaseResponse<EnableTagResponse> {

    @Override
    public String toString() {
        return "EnableTagResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
