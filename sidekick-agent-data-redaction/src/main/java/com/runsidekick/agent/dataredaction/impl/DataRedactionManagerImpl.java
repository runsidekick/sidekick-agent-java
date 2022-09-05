package com.runsidekick.agent.dataredaction.impl;

import com.runsidekick.agent.core.util.PropertyUtils;
import com.runsidekick.agent.core.util.StringUtils;
import com.runsidekick.agent.dataredaction.DataRedactionManager;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author yasin.kalafat
 */
public class DataRedactionManagerImpl implements DataRedactionManager {

    private static final Logger LOGGER = Logger.getLogger(DataRedactionManagerImpl.class.getName());

    private static final String DATA_REDACTOR_CLASS_NAME = "com.runsidekick.agent.dataredactor";
    private static final String CAPTURE_FRAME_DATA_REDACTION_CALLBACK_METHOD = "captureFrameDataRedactionCallback";
    private static final String LOG_MESSAGE_DATA_REDACTION_CALLBACK_METHOD = "logMessageDataRedactionCallback";

    private static final String CAPTURE_FRAME_DATA_REDACTION_CALLBACK =
            PropertyUtils.getStringProperty(
                    "sidekick.agent.captureFrameDataRedactionCallback");

    private static final String LOG_MESSAGE_DATA_REDACTION_CALLBACK =
            PropertyUtils.getStringProperty(
                    "sidekick.agent.logMessageDataRedactionCallback");

    @Override
    public void redactFrameData(Map<String, String> serializedVariables) {
        if (CAPTURE_FRAME_DATA_REDACTION_CALLBACK != null) {
            try {
                Object dataRedactor = getDataRedactor();
                Method method = dataRedactor.getClass().getMethod(CAPTURE_FRAME_DATA_REDACTION_CALLBACK_METHOD,
                        new Class[]{Map.class});
                method.invoke(dataRedactor, new Object[]{serializedVariables});
            } catch (Exception ex) {
                LOGGER.severe(String.format(
                        "Unable to redact frame data", ex.getMessage()));
            }
        }
    }

    @Override
    public String redactLogMessage(String fileName, int lineNo, String methodName,
                                   Map<String, String> serializedVariables, String logExpression, String logMessage) {
        if (LOG_MESSAGE_DATA_REDACTION_CALLBACK != null) {
            try {
                Object dataRedactor = getDataRedactor();
                Method method = dataRedactor.getClass().getMethod(LOG_MESSAGE_DATA_REDACTION_CALLBACK_METHOD,
                        new Class[]{String.class, int.class, String.class, Map.class, String.class, String.class});
                logMessage = (String) method.invoke(dataRedactor,
                        new Object[]{fileName, lineNo, methodName, serializedVariables, logExpression, logMessage});
            } catch (Exception ex) {
                LOGGER.severe(String.format(
                        "Unable to redact frame data", ex.getMessage()));
            }
        }
        return logMessage;
    }

    private Object getDataRedactor() throws CannotCompileException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass;
        try {
            pool.get(DATA_REDACTOR_CLASS_NAME);
        } catch (NotFoundException e) {
            ctClass = pool.makeClass(DATA_REDACTOR_CLASS_NAME);
            if (!StringUtils.isNullOrEmpty(CAPTURE_FRAME_DATA_REDACTION_CALLBACK)) {
                CtMethod ctMethod = CtNewMethod.make(CAPTURE_FRAME_DATA_REDACTION_CALLBACK, ctClass);
                ctClass.addMethod(ctMethod);
            }
            if (!StringUtils.isNullOrEmpty(LOG_MESSAGE_DATA_REDACTION_CALLBACK)) {
                CtMethod ctMethod = CtNewMethod.make(LOG_MESSAGE_DATA_REDACTION_CALLBACK, ctClass);
                ctClass.addMethod(ctMethod);
            }
            ctClass.toClass().newInstance();
        }

        Class<?> clazz = Class.forName(DATA_REDACTOR_CLASS_NAME);
        Constructor<?> ctor = clazz.getConstructor();
        return ctor.newInstance(new Object[]{});
    }
}
