package com.runsidekick.agent.instrument;

import com.runsidekick.agent.instrument.internal.InstrumentSupportInternal;
import com.runsidekick.agent.core.util.PropertyUtils;

import java.lang.instrument.Instrumentation;

/**
 * Support class for instrumentation related stuff.
 *
 * @author serkan
 */
public final class InstrumentSupport {

    public static final boolean INSTRUMENTATION_DISABLED =
            PropertyUtils.getBooleanProperty("sidekick.agent.instrument.disable", false);

    private static boolean initialized = false;
    private static volatile Instrumentation INSTRUMENTATION;

    public synchronized static boolean ensureActivated() {
        if (!initialized) {
            if (INSTRUMENTATION_DISABLED) {
                INSTRUMENTATION = null;
            } else {
                INSTRUMENTATION = InstrumentSupportInternal.attachAgent();
            }
            initialized = true;
        }
        return INSTRUMENTATION != null;
    }

    public synchronized static boolean activate(Instrumentation instrumentation, String arguments) {
        if (!initialized) {
            if (INSTRUMENTATION_DISABLED) {
                INSTRUMENTATION = null;
            } else {
                INSTRUMENTATION = instrumentation;
            }
            initialized = true;
        }
        return INSTRUMENTATION != null;
    }

    public static Instrumentation getInstrumentation() {
        ensureActivated();

        return INSTRUMENTATION;
    }

    public static void checkEnabled() {
//        if (!FeatureChecker.isFeatureEnabled("instrument", 100)) {
//            throw new IllegalStateException("'Instrumentation' support is not enabled");
//        }
    }

}
