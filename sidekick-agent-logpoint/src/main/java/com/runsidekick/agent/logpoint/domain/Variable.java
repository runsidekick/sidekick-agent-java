package com.runsidekick.agent.logpoint.domain;

/**
 * Holds variable with its name, type and value.
 *
 * @author yasin
 */
public class Variable {

    final String name;
    final String type;
    final Object value;

    public Variable(String name, Object value) {
        this.name = name;
        this.type = value == null ? null : value.getClass().getName();
        this.value = value;
    }

    public Variable(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                '}';
    }

}
