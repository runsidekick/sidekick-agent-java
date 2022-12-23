package com.runsidekick.agent.probetag.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author yasin.kalafat
 */
public class DisableProbeTagResponse extends BaseResponse<DisableProbeTagResponse> {

    @Override
    public String toString() {
        return "DisableProbeTagResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
