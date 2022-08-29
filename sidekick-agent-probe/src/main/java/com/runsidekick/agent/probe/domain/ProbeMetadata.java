package com.runsidekick.agent.probe.domain;

import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author serkan
 */
public class ProbeMetadata {

    private final ClassLoader classLoader;
    private final CtClass clazz;
    private final CtMethod method;
    private final ClassType classType;

    public ProbeMetadata(ClassLoader classLoader, CtClass clazz, CtMethod method, ClassType classType) {
        this.classLoader = classLoader;
        this.clazz = clazz;
        this.method = method;
        this.classType = classType;
    }

    public ClassLoader classLoader() {
        return classLoader;
    }

    public CtClass clazz() {
        return clazz;
    }

    public CtMethod method() {
        return method;
    }

    public ClassType classType() {
        return classType;
    }

}
