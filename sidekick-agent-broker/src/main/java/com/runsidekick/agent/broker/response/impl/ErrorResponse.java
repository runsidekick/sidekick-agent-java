package com.runsidekick.agent.broker.response.impl;

/**
 * @author serkan
 */
public class ErrorResponse extends BaseResponse {

    public ErrorResponse() {
    }

    public ErrorResponse(String requestId, String client, int errorCode, String errorMessage) {
        this.requestId = requestId;
        this.client = client;
        this.erroneous = true;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ErrorResponse(String requestId, String client, Throwable error) {
        this.requestId = requestId;
        this.client = client;
        setError(error);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "requestId='" + requestId + '\'' +
                ", client='" + client + '\'' +
                ", erroneous=" + erroneous +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
