package com.runsidekick.agent.probe.util;

import com.runsidekick.agent.probe.domain.ClassType;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.SourceFileAttribute;
import javassist.bytecode.annotation.Annotation;

/**
 * Utility class for providing probe related stuff.
 *
 * @author serkan
 */
public final class ProbeUtils {

    private ProbeUtils() {
    }

    public static ClassType getClassType(CtClass clazz) {
        if (clazz.hasAnnotation("kotlin.Metadata")) {
            return ClassType.KOTLIN;
        }
        SourceFileAttribute sourceFileAttribute =
                (SourceFileAttribute) clazz.getClassFile().getAttribute(SourceFileAttribute.tag);
        if (sourceFileAttribute != null) {
            String fileName = sourceFileAttribute.getFileName();
            if (fileName != null) {
                if (fileName.endsWith(".java")) {
                    return ClassType.JAVA;
                } else if (fileName.endsWith(".kt")) {
                    return ClassType.KOTLIN;
                } else if (fileName.endsWith(".scala")) {
                    return ClassType.SCALA;
                }
            }
        }
        AnnotationsAttribute annotationsAttribute =
                (AnnotationsAttribute) clazz.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation scalaSignatureAnnotation =
                    annotationsAttribute.getAnnotation("scala.reflect.ScalaSignature");
            if (scalaSignatureAnnotation != null) {
                return ClassType.SCALA;
            }
        }
        return ClassType.JAVA;
    }

}
