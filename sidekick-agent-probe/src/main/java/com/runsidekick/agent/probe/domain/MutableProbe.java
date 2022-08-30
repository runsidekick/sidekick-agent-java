package com.runsidekick.agent.probe.domain;

/**
 * @author serkan
 */
public interface MutableProbe extends Probe {

    void setRemoved(boolean removed);

    <A extends ProbeAction> A addAction(A action);

    <A extends ProbeAction> A replaceAction(A action);

    <A extends ProbeAction> A removeAction(String id);

}
