package com.runsidekick.agent.probe.domain;

/**
 * @author serkan
 */
public enum ClassType {

    JAVA("java"),
    KOTLIN("kt"),
    SCALA("scala");

    private final String extension;

    ClassType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

}
