package com.runsidekick.agent.logpoint.application;

import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.broker.application.ApplicationStatusProvider;
import com.runsidekick.agent.broker.domain.ApplicationStatus;

/**
 * @author yasin
 */
public class ApplicationStatusLogPointProvider implements ApplicationStatusProvider {

    @Override
    public void provide(ApplicationStatus applicationStatus, String client) {
        applicationStatus.addAttribute("logPoints", LogPointSupport.listLogPoints(client));
    }

}
