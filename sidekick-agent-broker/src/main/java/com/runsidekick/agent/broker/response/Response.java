package com.runsidekick.agent.broker.response;

import com.runsidekick.agent.core.util.EnvironmentUtils;

/**
 * @author serkan
 */
public interface Response {

    default String getType() {
        return "Response";
    }

    default String getSource(){return "Agent";}

    default String getRuntime(){return "JVM-" + EnvironmentUtils.JVM_VERSION;}

    default String getAgentVersion(){return EnvironmentUtils.AGENT_VERSION;}

    String getName();

    String getRequestId();

    String getClient();

    String getApplicationName();
    String getApplicationInstanceId();

    boolean isErroneous();
    int getErrorCode();
    String getErrorMessage();

}
