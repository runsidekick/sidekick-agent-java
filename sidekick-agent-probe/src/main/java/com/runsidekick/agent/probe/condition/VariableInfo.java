package com.runsidekick.agent.probe.condition;

/**
 * @author serkan
 */
public class VariableInfo {

    private final Class type;
    private final int idx;

    public VariableInfo(Class type, int idx) {
        this.type = type;
        this.idx = idx;
    }

    public Class getType() {
        return type;
    }

    public int getIndex() {
        return idx;
    }

    @Override
    public String toString() {
        return "VariableInfo{" +
                "type=" + type +
                ", index=" + idx +
                '}';
    }

}
