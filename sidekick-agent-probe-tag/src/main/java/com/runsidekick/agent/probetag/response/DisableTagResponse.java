package com.runsidekick.agent.probetag.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author yasin.kalafat
 */
public class DisableTagResponse extends BaseResponse<DisableTagResponse> {

    @Override
    public String toString() {
        return "DisableTagResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
