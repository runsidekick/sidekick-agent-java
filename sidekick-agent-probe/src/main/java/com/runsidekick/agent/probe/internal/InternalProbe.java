package com.runsidekick.agent.probe.internal;

import com.runsidekick.agent.probe.domain.MutableProbe;
import com.runsidekick.agent.probe.domain.impl.BaseProbe;
import com.runsidekick.agent.probe.domain.ClassType;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.ProbeMetadata;

import java.util.Objects;

/**
 * @author serkan
 */
class InternalProbe extends BaseProbe implements MutableProbe {

    final MethodProbes ownerMethodProbes;
    final String ownerClassName;
    final ClassType classType;
    final String methodId;

    InternalProbe(MethodProbes ownerMethodProbes, String ownerClassName, ClassType classType,
                  String id, String fileName, String className, int lineNo, String client, ProbeMetadata metadata,
                  String methodId) {
        super(id, fileName, className, lineNo, client, metadata.method().getName());
        this.ownerMethodProbes = ownerMethodProbes;
        this.ownerClassName = ownerClassName;
        this.classType = classType;
        this.methodId = methodId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    @Override
    public <A extends ProbeAction> A addAction(A action) {
        return (A) actions.putIfAbsent(action.id(), action);
    }

    @Override
    public <A extends ProbeAction> A replaceAction(A action) {
        return (A) actions.replace(action.id(), action);
    }

    @Override
    public <A extends ProbeAction> A removeAction(String id) {
        return (A) actions.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalProbe that = (InternalProbe) o;
        return lineNo == that.lineNo &&
                Objects.equals(className, that.className) &&
                Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, lineNo, client);
    }

}
