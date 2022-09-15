package com.runsidekick.agent.dataredaction;

import com.runsidekick.agent.api.dataredaction.DataRedactionContext;
import com.runsidekick.agent.api.dataredaction.SidekickDataRedactionAPI;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.PropertyUtils;
import com.runsidekick.agent.core.util.StringUtils;
import com.runsidekick.agent.core.util.map.ConcurrentWeakMap;
import org.slf4j.Logger;

import java.util.Map;

/**
 * @author yasin.kalafat
 */
public final class DataRedactionHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataRedactionHelper.class);

    private static final String DATA_REDACTION_IMPL_CLASS =
            PropertyUtils.getStringProperty("sidekick.agent.dataredactionimplclass");

    private static final ConcurrentWeakMap<ClassLoader, SidekickDataRedactionAPI> sidekickDataRedactionAPICache =
            new ConcurrentWeakMap();

    private DataRedactionHelper() {}

    public static boolean shouldRedactVariable(DataRedactionContext dataRedactionContext, String fieldName) {
        if (!StringUtils.isNullOrEmpty(DATA_REDACTION_IMPL_CLASS)) {
            if (fieldName != null) {
                try {
                    SidekickDataRedactionAPI dataRedactionInstance = getDataRedactionInstance(
                            getClassLoader(dataRedactionContext));
                    return dataRedactionInstance.shouldRedactVariable(dataRedactionContext, fieldName);
                } catch (Exception ex) {
                    LOGGER.error("Unable to redact variable", ex);
                }
            }
        }
        return false;
    }

    public static String redactLogMessage(
            DataRedactionContext dataRedactionContext, String logExpression, String logMessage) {
        if (!StringUtils.isNullOrEmpty(DATA_REDACTION_IMPL_CLASS)) {
            try {
                SidekickDataRedactionAPI dataRedactionInstance = getDataRedactionInstance(
                        getClassLoader(dataRedactionContext));
                return dataRedactionInstance.redactLogMessage(dataRedactionContext, logExpression,
                        logMessage);
            } catch (Exception ex) {
                LOGGER.error("Unable to redact log message", ex);
            }
        }
        return logMessage;
    }

    private static ClassLoader getClassLoader(DataRedactionContext dataRedactionContext) {
        if (dataRedactionContext != null && dataRedactionContext.getClazz() != null) {
            return dataRedactionContext.getClazz().getClassLoader();
        }
        return DataRedactionHelper.class.getClassLoader();
    }

    private static SidekickDataRedactionAPI getDataRedactionInstance(ClassLoader classLoader) throws Exception {
        SidekickDataRedactionAPI sidekickDataRedactionAPI = sidekickDataRedactionAPICache.get(classLoader);
        if (sidekickDataRedactionAPI == null) {
            sidekickDataRedactionAPI = createSidekickDataRedactionInstance(classLoader);
            SidekickDataRedactionAPI existingSidekickDataRedactionAPI =
                    sidekickDataRedactionAPICache.putIfAbsent(classLoader, sidekickDataRedactionAPI);
            if (existingSidekickDataRedactionAPI != null) {
                sidekickDataRedactionAPI = existingSidekickDataRedactionAPI;
            }
        }
        return sidekickDataRedactionAPI;
    }

    private static SidekickDataRedactionAPI createSidekickDataRedactionInstance(ClassLoader classLoader)
            throws Exception {
        Class dataRedactionImplClazz;
        if (classLoader != null) {
            dataRedactionImplClazz = Class.forName(DATA_REDACTION_IMPL_CLASS, false, classLoader);
        } else {
            dataRedactionImplClazz = Class.forName(DATA_REDACTION_IMPL_CLASS);
        }
        Object dataRedactionImplInstance = dataRedactionImplClazz.getConstructor().newInstance(new Object[]{});
        return (SidekickDataRedactionAPI) dataRedactionImplInstance;
    }
}
