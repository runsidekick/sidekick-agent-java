package com.runsidekick.agent.probe;

import com.runsidekick.agent.broker.event.Event;
import com.runsidekick.agent.probe.condition.Condition;
import com.runsidekick.agent.probe.domain.ClassType;
import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.ProbeMetadata;
import com.runsidekick.agent.probe.internal.ProbeManager;
import com.runsidekick.agent.broker.BrokerManager;
import com.runsidekick.agent.core.logger.LoggerFactory;
import javassist.CtClass;
import javassist.CtMethod;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * @author serkan
 */
public final class ProbeSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProbeSupport.class);

    private static boolean initialized;

    private ProbeSupport() {
    }

    //////////////////////////////////////////////////////////////////////////////////

    public synchronized static void ensureInitialized() {
        if (!initialized) {
            doInitialize();
            initialized = true;
        }
    }

    private static void doInitialize() {
        ProbeManager.ensureInitialized();
    }

    public static ProbeMetadata getProbeMetadata(String className, int lineNo, String client) throws Exception {
        return ProbeManager.getProbeMetadata(className, lineNo, client);
    }

    public static ProbeMetadata getProbeMetadata(Probe probe) throws Exception {
        return ProbeManager.getProbeMetadata(probe);
    }

    public static Probe getProbe(String id) {
        return ProbeManager.getProbe(id);
    }

    public static Probe getProbe(String className, int lineNo, String client) {
        return ProbeManager.getProbe(className, lineNo, client);
    }

    public static Probe getOrPutProbe(String fileName, String className, int lineNo, String client) {
        if (client == null) {
            client = BrokerManager.BROKER_CLIENT;
        }
        return ProbeManager.getOrPutProbe(fileName, className, lineNo, client);
    }

    public static void removeProbe(String id, boolean ifEmpty) {
        ProbeManager.removeProbe(id, ifEmpty);
    }

    public static String getSourceCodeHash(ClassLoader classLoader, CtClass clazz,
                                           ClassType classType, String className) throws IOException {
        return ProbeManager.getSourceCodeHash(classLoader, clazz, classType, className);
    }

    public static Condition getCondition(String conditionExpression,
                                         String className, ClassLoader classLoader, ClassType classType,
                                         CtMethod method, int lineNo) {
        return ProbeManager.getCondition(conditionExpression, className, classLoader, classType, method, lineNo);
    }

    public static <A extends ProbeAction> A addProbeAction(Probe probe, A action) {
        return ProbeManager.addProbeAction(probe, action);
    }

    public static <A extends ProbeAction> A replaceProbeAction(Probe probe, A action) {
        return ProbeManager.replaceProbeAction(probe, action);
    }

    public static <A extends ProbeAction> A removeProbeAction(Probe probe, String id) {
        return ProbeManager.removeProbeAction(probe, id);
    }

    public static void publishProbeEvent(Event event) {
        BrokerManager.serializeAndPublishEvent(event);
    }

}
