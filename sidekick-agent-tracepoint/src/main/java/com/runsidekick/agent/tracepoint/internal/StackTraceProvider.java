package com.runsidekick.agent.tracepoint.internal;

import com.runsidekick.agent.core.util.ClassUtils;
import com.runsidekick.agent.core.util.PropertyUtils;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

import java.util.Arrays;

/**
 * @author serkan
 */
class StackTraceProvider {

    // Only available until Java 8, not available for getting stacktrace since Java 9
    private static final boolean useSharedSecrets = ClassUtils.hasClass("sun.misc.SharedSecrets");
    private static final int MAX_STACKTRACE_DEPTH =
            PropertyUtils.getIntegerProperty(
                    "sidekick.agent.tracepoint.stacktrace.maxdepth", Integer.MAX_VALUE);

    static StackTraceElement[] getStackTrace(Throwable throwable, int baseFrameCount) {
        // TODO Use StackWalker API for Java 9+ when possible
        if (throwable == null) {
            throwable = new Throwable();
        }
        if (useSharedSecrets) {
            JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
            if (javaLangAccess != null) {
                int depth = javaLangAccess.getStackTraceDepth(throwable);
                int startFrame = baseFrameCount + 1;
                int frameCount = depth - startFrame;
                int effectiveFrameCount = Math.min(frameCount, MAX_STACKTRACE_DEPTH);
                if (effectiveFrameCount < frameCount) {
                    StackTraceElement[] stackTraceElements = new StackTraceElement[effectiveFrameCount];
                    for (int i = 0; i < effectiveFrameCount; i++) {
                        StackTraceElement stackTraceElement =
                                javaLangAccess.getStackTraceElement(throwable, startFrame + i);
                        stackTraceElements[i] = stackTraceElement;
                    }
                    return stackTraceElements;
                }
            }
        }
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        return Arrays.copyOfRange(stackTraceElements, baseFrameCount + 1, stackTraceElements.length);
    }

}
