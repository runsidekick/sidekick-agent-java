package com.runsidekick.agent.broker.application;

import com.runsidekick.agent.broker.domain.ApplicationStatus;

/**
 * @author serkan
 */
public interface ApplicationStatusProvider {

    void provide(ApplicationStatus applicationStatus, String client);

}
