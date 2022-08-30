package com.runsidekick.agent.broker.response;

/**
 * @author serkan
 */
public interface MutableResponse<R extends Response> extends Response {

    R setRequestId(String requestId);

    R setClient(String client);

    R setApplicationName(String applicationName);
    R setApplicationInstanceId(String applicationInstanceId);

    R setErroneous(boolean erroneous);
    R setErrorCode(int errorCode);
    R setErrorMessage(String errorMessage);
    R setError(Throwable error);

}
