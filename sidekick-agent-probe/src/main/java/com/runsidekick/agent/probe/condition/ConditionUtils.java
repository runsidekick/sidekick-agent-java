package com.runsidekick.agent.probe.condition;

/**
 * @author serkan
 */
public final class ConditionUtils {

    private ConditionUtils() {
    }

    public static boolean isBooleanType(Class type) {
        return boolean.class.equals(type) || Boolean.class.equals(type);
    }

    public static boolean isCharacterType(Class type) {
        return char.class.equals(type) || Character.class.equals(type);
    }

    public static boolean isNumberType(Class type) {
        return byte.class.equals(type) || Byte.class.equals(type)
                || short.class.equals(type) || Short.class.equals(type)
                || int.class.equals(type) || Integer.class.equals(type)
                || float.class.equals(type) || Float.class.equals(type)
                || long.class.equals(type) || Long.class.equals(type)
                || double.class.equals(type) || Double.class.equals(type);
    }

    public static boolean isStringType(Class type) {
        return String.class.equals(type);
    }

}
