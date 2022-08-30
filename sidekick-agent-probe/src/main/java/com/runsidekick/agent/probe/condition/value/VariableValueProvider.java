package com.runsidekick.agent.probe.condition.value;

import com.runsidekick.agent.probe.condition.ConditionContext;
import com.runsidekick.agent.probe.error.ProbeErrorCodes;
import com.runsidekick.agent.broker.error.CodedException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author serkan
 */
public class VariableValueProvider<V> implements ValueProvider<V> {

    private final ThreadLocal<VariableMetadata> threadLocalValueMetadata =
            ThreadLocal.withInitial(() -> new VariableMetadata());
    private final int varIdx;
    private final String propPath;
    private final String[] props;

    public VariableValueProvider(int varIdx, String propPath) {
        this.varIdx = varIdx;
        this.propPath = propPath;
        if (propPath == null) {
            this.props = null;
        } else {
            this.props = propPath.split("\\.");
        }
    }

    @Override
    public V getValue(ConditionContext conditionContext) {
        Object value = conditionContext.getVariableValue(varIdx);
        if (props == null) {
            return (V) value;
        } else {
            return (V) getValueFromProps(value);
        }
    }

    private Field getPropField0(Class clazz, String prop) {
        Field propField = null;
        while (propField == null && !clazz.equals(Object.class)) {
            try {
                propField = clazz.getDeclaredField(prop);
            } catch (NoSuchFieldException e) {
            }
            clazz = clazz.getSuperclass();
        }
        if (propField != null) {
            propField.setAccessible(true);
        }
        return propField;
    }

    private Field getPropField(Class clazz, String prop) {
        VariableMetadata variableMetadata = threadLocalValueMetadata.get();
        ClassProp classProp = new ClassProp(clazz, prop);
        Field propField = variableMetadata.classPropFieldMap.get(classProp);
        // TODO Distinguish whether prop is not exist in the cache or it couldn't found in the previous lookups
        if (propField == null) {
            propField = getPropField0(clazz, prop);
            variableMetadata.classPropFieldMap.put(classProp, propField);
        }
        return propField;
    }

    private Object getValueFromProps(Object value) {
        Object propValue = null;
        Object propOwnerValue = value;
        Class propOwnerClazz = propOwnerValue.getClass();
        for (int i = 0; i < props.length; i++) {
            String prop = props[i];
            Field propField = getPropField(propOwnerClazz, prop);
            if (propField == null) {
                String pp = generatePropPath(i);
                throw new CodedException(ProbeErrorCodes.UNABLE_TO_FIND_PROPERTY_FOR_CONDITION, propOwnerClazz.getName(), pp);
            }
            try {
                propValue = propField.get(propOwnerValue);
                propOwnerValue = propValue;
                propOwnerClazz = propOwnerValue.getClass();
            } catch (Exception e) {
                String pp = generatePropPath(i);
                throw new CodedException(ProbeErrorCodes.UNABLE_TO_GET_PROPERTY_FOR_CONDITION, e, propOwnerClazz.getName(), pp);
            }
        }
        return propValue;
    }

    private String generatePropPath(int idx) {
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i <= idx; i++) {
            if (i > 0) {
                sb.append(".");
            }
            sb.append(props[i]);
        }
        return sb.toString();
    }

    private static class ClassProp {

        private final Class clazz;
        private final String propName;

        private ClassProp(Class clazz, String propName) {
            this.clazz = clazz;
            this.propName = propName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassProp classProp = (ClassProp) o;
            return Objects.equals(clazz, classProp.clazz) &&
                    Objects.equals(propName, classProp.propName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, propName);
        }

    }

    private static class VariableMetadata {

        private final Map<ClassProp, Field> classPropFieldMap = new HashMap<>();

    }

}
