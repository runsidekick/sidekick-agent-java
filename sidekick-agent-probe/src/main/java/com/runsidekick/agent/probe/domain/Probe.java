package com.runsidekick.agent.probe.domain;

import java.util.Collection;
import java.util.Set;

/**
 * @author serkan
 */
public interface Probe {

    String getId();

    String getFileName();

    String getClassName();

    int getLineNo();

    String getClient();

    String getMethodName();

    Set<String> getTags();

    <A extends ProbeAction> A getAction(String id);

    Collection<ProbeAction> actions();

    boolean hasAnyAction();

    boolean isRemoved();

}
