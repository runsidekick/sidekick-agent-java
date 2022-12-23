package com.runsidekick.agent.probetag.response;

import com.runsidekick.agent.broker.response.impl.BaseResponse;

/**
 * @author yasin.kalafat
 */
public class EnableProbeTagResponse extends BaseResponse<EnableProbeTagResponse> {

    @Override
    public String toString() {
        return "EnableProbeTagResponse{" +
                "requestId='" + requestId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
