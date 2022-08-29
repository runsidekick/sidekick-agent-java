package com.runsidekick.agent.probe.internal;

import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.probe.domain.ClassType;
import com.runsidekick.agent.probe.condition.Condition;
import com.runsidekick.agent.probe.condition.ConditionFactory;
import com.runsidekick.agent.probe.condition.VariableInfo;
import com.runsidekick.agent.probe.condition.VariableInfoProvider;
import com.runsidekick.agent.probe.error.ProbeErrorCodes;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LineNumberAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * @author serkan
 */
final class ConditionHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionHelper.class);

    private ConditionHelper() {
    }

    static Condition getCondition(String conditionExpression,
                                  String className, ClassLoader classLoader, ClassType classType,
                                  CtMethod method, int lineNo) {
        try {
            MethodInfo methodInfo = method.getMethodInfo();
            CodeAttribute codeAttr = methodInfo.getCodeAttribute();
            if (codeAttr == null) {
                String errorMessage =
                        String.format(
                                "No code info could be found in class %s. So probes are not supported",
                                className);
                LOGGER.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            LineNumberAttribute lineNoAttr =
                    (LineNumberAttribute) codeAttr.getAttribute(LineNumberAttribute.tag);
            if (lineNoAttr == null) {
                String errorMessage =
                        String.format(
                                "No line number info could be found in class %s. So probes are not supported",
                                className);
                LOGGER.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            LocalVariableAttribute localVarAttr =
                    (LocalVariableAttribute) codeAttr.getAttribute(LocalVariableAttribute.tag);
            if (localVarAttr == null) {
                String errorMessage =
                        String.format(
                                "No local variable info could be found in class %s. So probes are not supported",
                                className);
                LOGGER.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            Collection<LocalVarMetadata> localVarMetadatas =
                    ProbeManager.extractLocalVarMetadata(classType, lineNoAttr, localVarAttr, lineNo);
            VariableInfoProvider variableInfoProvider = variableName -> {
                int i = 0;
                int localVarMetadataIdx = -1;
                LocalVarMetadata localVarMetadata = null;
                for (LocalVarMetadata lvm : localVarMetadatas) {
                    if (lvm.getOriginalName().equals(variableName)) {
                        localVarMetadataIdx = i;
                        localVarMetadata = lvm;
                        break;
                    }
                    i++;
                }
                if (localVarMetadata == null) {
                    return null;
                }
                Class localVarType;
                try {
                    localVarType = getLocalVariableType(localVarMetadata.getTypeSignature(), classLoader);
                } catch (ClassNotFoundException e) {
                    LOGGER.error(
                            "Unable to find class represented by type descriptor {} which checking type of variable {}",
                            localVarMetadata.getTypeSignature(), localVarMetadata.getName());
                    return null;
                }
                return new VariableInfo(localVarType, localVarMetadataIdx);
            };
            return ConditionFactory.createConditionFromExpression(conditionExpression, variableInfoProvider);
        } catch (Throwable t) {
            if (t instanceof CodedException) {
                throw (CodedException) t;
            } else {
                throw new CodedException(
                        ProbeErrorCodes.CONDITION_CHECK_FAILED, conditionExpression, t.getMessage());
            }
        }
    }

    private static Class getLocalVariableType(String localVarTypeSignature, ClassLoader classLoader)
            throws ClassNotFoundException {
        switch (localVarTypeSignature) {
            case "Z":
                return boolean.class;
            case "Ljava/lang/Boolean;":
                return Boolean.class;
            case "B":
                return byte.class;
            case "Ljava/lang/Byte;":
                return Byte.class;
            case "C":
                return char.class;
            case "Ljava/lang/Character;":
                return Character.class;
            case "S":
                return short.class;
            case "Ljava/lang/Short;":
                return Short.class;
            case "I":
                return int.class;
            case "Ljava/lang/Integer;":
                return Integer.class;
            case "F":
                return float.class;
            case "Ljava/lang/Float;":
                return Float.class;
            case "J":
                return long.class;
            case "Ljava/lang/Long;":
                return Long.class;
            case "D":
                return double.class;
            case "Ljava/lang/Double;":
                return Double.class;
            case "Ljava/lang/String;":
                return String.class;
            default:
                // No need to try to find and get actual class
                return Object.class; //ClassUtils.getClassWithException(classLoader, localVarTypeSignature, false);
        }
    }

}
