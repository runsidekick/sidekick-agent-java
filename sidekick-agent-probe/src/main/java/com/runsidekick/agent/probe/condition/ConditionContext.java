package com.runsidekick.agent.probe.condition;

/**
 * @author serkan
 */
public class ConditionContext {

    private final Class<?> clazz;
    private final Object obj;
    private final Object[] variables;

    public ConditionContext(Object[] variables) {
        this.clazz = null;
        this.obj = null;
        this.variables = variables;
    }

    public ConditionContext(Class<?> clazz, Object obj,
                            Object[] variables) {
        this.clazz = clazz;
        this.obj = obj;
        this.variables = variables;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getObj() {
        return obj;
    }

    public ClassLoader getClassLoader() {
        if (clazz != null) {
            return clazz.getClassLoader();
        }
        return null;
    }

    public <V> V getVariableValue(int varIdx) {
        if (variables != null && variables.length > varIdx) {
            return (V) variables[varIdx];
        }
        return null;
    }

}
