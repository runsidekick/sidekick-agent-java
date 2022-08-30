package com.runsidekick.agent.tracepoint.application;

import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.broker.application.ApplicationStatusProvider;
import com.runsidekick.agent.broker.domain.ApplicationStatus;

/**
 * @author serkan
 */
public class ApplicationStatusTracePointProvider implements ApplicationStatusProvider {

    @Override
    public void provide(ApplicationStatus applicationStatus, String client) {
        applicationStatus.addAttribute("tracePoints", TracePointSupport.listTracePoints(client));
    }

}
