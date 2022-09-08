package com.runsidekick.agent.api.dataredaction;

import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.PropertyUtils;
import com.runsidekick.agent.core.util.StringUtils;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * @author yasin.kalafat
 */
public final class DataRedactionHelper {

    private DataRedactionHelper() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DataRedactionHelper.class);

    private static final String DATA_REDACTION_IMPL_CLASS =
            PropertyUtils.getStringProperty("sidekick.agent.dataredactionimplclass");

    public static void redactVariable(DataRedactionContext dataRedactionContext) {
        if (!StringUtils.isNullOrEmpty(DATA_REDACTION_IMPL_CLASS)) {
            try {
                SidekickDataRedactionAPI dataRedactionInstance = getDataRedactionInstance(dataRedactionContext);
                dataRedactionInstance.test();
            } catch (Exception ex) {
                LOGGER.error(String.format("Unable to redact variable", ex.getMessage()));
            }
        }
    }

    public static String redactLogMessage(
            DataRedactionContext dataRedactionContext, Map<String, String> serializedVariables, String logExpression,
            String logMessage) {
        if (!StringUtils.isNullOrEmpty(DATA_REDACTION_IMPL_CLASS)) {
            try {
                SidekickDataRedactionAPI dataRedactionInstance = getDataRedactionInstance(dataRedactionContext);
                dataRedactionInstance.test();
            } catch (Exception ex) {
                LOGGER.error(String.format("Unable to redact log message", ex.getMessage()));
            }
        }
        return logMessage;
    }

    private static SidekickDataRedactionAPI getDataRedactionInstance(DataRedactionContext dataRedactionContext)
            throws Exception {
        Class dataRedactionImplClazz = Class.forName(DATA_REDACTION_IMPL_CLASS, false,
                dataRedactionContext.getClazz().getClassLoader());
        Constructor<?> ctor = dataRedactionImplClazz.getConstructor();
        return (SidekickDataRedactionAPI) ctor.newInstance(new Object[]{});
    }

}
