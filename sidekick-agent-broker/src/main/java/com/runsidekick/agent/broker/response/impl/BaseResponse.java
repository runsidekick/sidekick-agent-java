package com.runsidekick.agent.broker.response.impl;

import com.runsidekick.agent.broker.response.Response;
import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.broker.error.CommonErrorCodes;
import com.runsidekick.agent.broker.response.MutableResponse;

/**
 * @author serkan
 */
public abstract class BaseResponse<R extends Response> implements MutableResponse<R> {

    protected String requestId;
    protected String client;
    protected String applicationName;
    protected String applicationInstanceId;
    protected boolean erroneous;
    protected int errorCode;
    protected String errorMessage;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public R setRequestId(String requestId) {
        this.requestId = requestId;
        return (R) this;
    }

    @Override
    public String getClient() {
        return client;
    }

    @Override
    public R setClient(String client) {
        this.client = client;
        return (R) this;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public R setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return (R) this;
    }

    @Override
    public String getApplicationInstanceId() {
        return applicationInstanceId;
    }

    @Override
    public R setApplicationInstanceId(String applicationInstanceId) {
        this.applicationInstanceId = applicationInstanceId;
        return (R) this;
    }

    @Override
    public boolean isErroneous() {
        return erroneous;
    }

    @Override
    public R setErroneous(boolean erroneous) {
        this.erroneous = erroneous;
        return (R) this;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public R setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return (R) this;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public R setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return (R) this;
    }

    @Override
    public R setError(Throwable error) {
        this.erroneous = true;
        this.errorCode =
                error instanceof CodedException
                        ? ((CodedException) error).getCode()
                        : CommonErrorCodes.UNKNOWN.getCode();
        this.errorMessage = error.getMessage();
        return (R) this;
    }

}
