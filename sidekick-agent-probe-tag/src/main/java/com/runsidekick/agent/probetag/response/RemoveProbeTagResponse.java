package com.runsidekick.agent.probetag.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author yasin.kalafat
 */
public class RemoveProbeTagResponse extends BaseResponse<RemoveProbeTagResponse> {

    @Override
    public String toString() {
        return "RemoveProbeTagResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
