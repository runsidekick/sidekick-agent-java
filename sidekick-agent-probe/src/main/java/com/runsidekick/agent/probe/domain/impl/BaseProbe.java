package com.runsidekick.agent.probe.domain.impl;

import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author serkan
 */
public abstract class BaseProbe implements Probe {

    protected final String id;
    protected final String fileName;
    protected final String className;
    protected final int lineNo;
    protected final String client;
    protected final String methodName;
    protected final Map<String, ProbeAction> actions = new ConcurrentHashMap<>();
    protected volatile boolean removed;

    protected BaseProbe(String id, String fileName, String className, int lineNo, String client,
                        String methodName) {
        this.id = id;
        this.fileName = fileName;
        this.className = className;
        this.lineNo = lineNo;
        this.client = client;
        this.methodName = methodName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public int getLineNo() {
        return lineNo;
    }

    @Override
    public String getClient() {
        return client;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public <A extends ProbeAction> A getAction(String id) {
        return (A) actions.get(id);
    }

    @Override
    public Collection<ProbeAction> actions() {
        return Collections.unmodifiableCollection(actions.values());
    }

    @Override
    public boolean hasAnyAction() {
        return !actions.isEmpty();
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

}
