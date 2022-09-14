package com.runsidekick.agent.probe.serialization;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author serkan
 */
public final class DeserializationHelper {

    private static final String TYPE_PROPERTY = "@type";
    private static final String VALUE_PROPERTY = "@value";
    private static final String ARRAY_PROPERTY = "@array";
    private static final String ARRAY_ELEMENT_TYPE_PROPERTY = "@array-element-type";
    private static final String ENUM_PROPERTY = "@enum";
    private static final String MAP_PROPERTY = "@map";
    private static final String IGNORED_PROPERTY = "@ignored";

    private DeserializationHelper() {
    }

    public static Variable parseVariable(String json) {
        return parseVariable(null, json);
    }

    public static Variable parseVariable(String variableName, String json) {
        if (json.charAt(0) == '[' && json.charAt(json.length() - 1) == ']') {
            return doParse(variableName, new JSONArray(json));
        } else {
            return doParse(variableName, new JSONObject(json));
        }
    }

    private static Variable doParse(String propName, Object prop) {
        return doParse(propName, null, null, prop);
    }

    private static Variable doParse(String propName, String propType, TypeCategory propTypeCategory, Object prop) {
        if (prop instanceof Boolean) {
            BooleanValue value = new BooleanValue(boolean.class.getName(), (boolean) prop);
            return new Variable(propName, value);
        } else if (prop instanceof String) {
            StringValue value = new StringValue((String) prop);
            return new Variable(propName, value);
        } else if (prop instanceof Number) {
            NumberValue value = new NumberValue(Number.class.getName(), (Number) prop);
            return new Variable(propName, value);
        } else if (prop instanceof JSONObject) {
            JSONObject propJsonObject = (JSONObject) prop;
            if (propType == null && !propJsonObject.has(TYPE_PROPERTY)) {
                return parseObjectType(propName, null, propTypeCategory, propJsonObject);
            }
            if (propType == null) {
                propType = propJsonObject.getString(TYPE_PROPERTY);
            }
            Variable variable;
            variable = parsePrimitiveTypes(propName, propType, propJsonObject);
            if (variable != null) {
                return variable;
            }
            variable = parseStringType(propName, propType, propJsonObject);
            if (variable != null) {
                return variable;
            }
            variable = parseEnumType(propName, propType, propJsonObject);
            if (variable != null) {
                return variable;
            }
            variable = parseObjectType(propName, propType, propTypeCategory, propJsonObject);
            if (variable != null) {
                return variable;
            }
        } else if (prop instanceof JSONArray) {
            return parseCollection(propName, propType, null, (JSONArray) prop);
        } else {
            if (prop == JSONObject.NULL) {
                prop = null;
            }
            TypelessValue value = new TypelessValue(prop);
            return new Variable(propName, value);
        }
        return null;
    }

    private static Variable parsePrimitiveTypes(String propName, String propType, JSONObject propJsonObject) {
        if (boolean.class.getName().equals(propType) || Boolean.class.getName().equals(propType)) {
            Boolean boolValue = null;
            if (!propJsonObject.isNull(VALUE_PROPERTY)) {
                boolValue = propJsonObject.getBoolean(VALUE_PROPERTY);
            }
            BooleanValue value = new BooleanValue(propType, boolValue);
            return new Variable(propName, value);
        }
        if (byte.class.getName().equals(propType) || Byte.class.getName().equals(propType) ||
                short.class.getName().equals(propType) || Short.class.getName().equals(propType) ||
                int.class.getName().equals(propType) || Integer.class.getName().equals(propType) ||
                long.class.getName().equals(propType) || Long.class.getName().equals(propType)) {
            Number numValue = null;
            if (!propJsonObject.isNull(VALUE_PROPERTY)) {
                numValue = propJsonObject.getLong(VALUE_PROPERTY);
            }
            NumberValue value = new NumberValue(propType, numValue);
            return new Variable(propName, value);
        }
        if (float.class.getName().equals(propType) || Float.class.getName().equals(propType) ||
                double.class.getName().equals(propType) || Double.class.getName().equals(propType)) {
            Number numValue = null;
            if (!propJsonObject.isNull(VALUE_PROPERTY)) {
                numValue = propJsonObject.getDouble(VALUE_PROPERTY);
            }
            NumberValue value = new NumberValue(propType, numValue);
            return new Variable(propName, value);
        }
        return null;
    }

    private static Variable parseStringType(String propName, String propType, JSONObject propJsonObject) {
        if (String.class.getName().equals(propType)) {
            String strValue = null;
            if (!propJsonObject.isNull(VALUE_PROPERTY)) {
                strValue = propJsonObject.getString(VALUE_PROPERTY);
            }
            StringValue value = new StringValue(strValue);
            return new Variable(propName, value);
        }
        return null;
    }

    private static Variable parseEnumType(String propName, String propType, JSONObject propJsonObject) {
        if (propJsonObject.has(ENUM_PROPERTY)) {
            String strValue = null;
            if (!propJsonObject.isNull(VALUE_PROPERTY)) {
                strValue = propJsonObject.getString(VALUE_PROPERTY);
            }
            EnumValue value = new EnumValue(propType, strValue);
            return new Variable(propName, value);
        }
        return null;
    }

    private static Variable parseObjectType(String propName, String propType, TypeCategory typeCategory,
                                            JSONObject propJsonObject) {
        if (propJsonObject.has(VALUE_PROPERTY)) {
            if (propJsonObject.has(ARRAY_PROPERTY) && propJsonObject.getBoolean(ARRAY_PROPERTY)) {
                JSONArray valueJsonArray = propJsonObject.getJSONArray(VALUE_PROPERTY);
                String elementType =
                        propJsonObject.has(ARRAY_ELEMENT_TYPE_PROPERTY)
                                ? propJsonObject.getString(ARRAY_ELEMENT_TYPE_PROPERTY)
                                : null;
                return parseCollection(propName, propType, elementType, valueJsonArray);
            } else {
                TypeCategory propTypeCategory = propJsonObject.has(MAP_PROPERTY) ? TypeCategory.MAP : null;
                return doParse(propName, propType, propTypeCategory, propJsonObject.get(VALUE_PROPERTY));
            }
        }
        List<Variable> subVariables = new ArrayList<>();
        for (String subPropName : propJsonObject.keySet()) {
            if (subPropName.startsWith("@")) {
                continue;
            }
            Object subProp = propJsonObject.get(subPropName);
            Variable subVariable = doParse(subPropName, subProp);
            if (subVariable != null) {
                subVariables.add(subVariable);
            }
        }
        typeCategory = typeCategory != null ? typeCategory : TypeCategory.OBJECT;
        ObjectValue value = new ObjectValue(propType, typeCategory, subVariables);
        return new Variable(propName, value);
    }

    private static Variable parseCollection(String propName, String propType,
                                            String elementType, JSONArray jsonArray) {
        int length = jsonArray.length();
        List<Variable> elementVariables = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Object element = jsonArray.get(i);
            Variable elementVariable;
            if (element instanceof Boolean) {
                if (elementType == null) {
                    elementType = Boolean.class.getName();
                }
                elementVariable =
                        new Variable(null, new BooleanValue(elementType, (Boolean) element));
            } else if (element instanceof Number) {
                if (elementType == null) {
                    elementType = Number.class.getName();
                }
                elementVariable =
                        new Variable(null, new NumberValue(elementType, (Number) element));
            } else if (element instanceof String) {
                elementVariable =
                        new Variable(null, new StringValue((String) element));
            } else {
                elementVariable = doParse(null, element);
            }
            if (elementVariable != null) {
                elementVariables.add(elementVariable);
            }
        }
        TypeCategory typeCategory =
                (propType != null && propType.endsWith("[]"))
                        ? TypeCategory.ARRAY
                        : TypeCategory.COLLECTION;
        ObjectValue value = new ObjectValue(propType, typeCategory, elementVariables);
        return new Variable(propName, value);
    }

    public static class Variable {

        private final String name;
        private final Value value;

        public Variable(String name, Value value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Value getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Variable{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    '}';
        }

    }

    public enum TypeCategory {

        UNKNOWN,
        RAW,
        BOOLEAN,
        NUMBER,
        STRING,
        OBJECT,
        ENUM,
        ARRAY,
        COLLECTION,
        MAP,

    }

    public static abstract class Value<V> {

        protected final String type;
        protected final TypeCategory typeCategory;
        protected final V value;

        public Value(String type, TypeCategory typeCategory, V value) {
            this.type = type;
            this.typeCategory = typeCategory;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public TypeCategory getTypeCategory() {
            return typeCategory;
        }

        public V getValue() {
            return value;
        }

    }

    public static class BooleanValue extends Value<Boolean> {

        public BooleanValue(String type, Boolean value) {
            super(type, TypeCategory.BOOLEAN, value);
        }

        @Override
        public String toString() {
            return "BooleanValue{" +
                    "type='" + type + '\'' +
                    ", typeCategory=" + typeCategory +
                    ", value=" + value +
                    '}';
        }

    }

    public static class NumberValue extends Value<Number> {

        public NumberValue(String type, Number value) {
            super(type, TypeCategory.NUMBER, value);
        }

        @Override
        public String toString() {
            return "NumberValue{" +
                    "type='" + type + '\'' +
                    ", typeCategory=" + typeCategory +
                    ", value=" + value +
                    '}';
        }

    }

    public static class StringValue extends Value<String> {

        public StringValue(String value) {
            super(String.class.getName(), TypeCategory.STRING, value);
        }

        @Override
        public String toString() {
            return "StringValue{" +
                    "type='" + type + '\'' +
                    ", typeCategory=" + typeCategory +
                    ", value=" + value +
                    '}';
        }

    }

    public static class ObjectValue extends Value<List<Variable>> {

        public ObjectValue(String type, List<Variable> variables) {
            super(type, TypeCategory.OBJECT, variables);
        }

        public ObjectValue(String type, TypeCategory typeCategory, List<Variable> variables) {
            super(type, typeCategory, variables);
        }

        @Override
        public String toString() {
            return "ObjectValue{" +
                    "type='" + type + '\'' +
                    ", typeCategory=" + typeCategory +
                    ", value=" + value +
                    '}';
        }

    }

    public static class EnumValue extends Value<String> {

        public EnumValue(String type, String value) {
            super(type, TypeCategory.ENUM, value);
        }

        @Override
        public String toString() {
            return "EnumValue{" +
                    "type='" + type + '\'' +
                    ", typeCategory=" + typeCategory +
                    ", value=" + value +
                    '}';
        }

    }

    public static class TypelessValue extends Value<Object> {

        public TypelessValue(Object value) {
            super(null, TypeCategory.UNKNOWN, value);
        }

        @Override
        public String toString() {
            return "TypelessValue{" +
                    "type='" + type + '\'' +
                    ", typeCategory=" + typeCategory +
                    ", value=" + value +
                    '}';
        }

    }

    public static class RawValue extends Value<Object> {

        public RawValue(String type, Object value) {
            super(type, TypeCategory.RAW, value);
        }

        @Override
        public String toString() {
            return "RawValue{" +
                    "type='" + type + '\'' +
                    ", typeCategory=" + typeCategory +
                    ", value=" + value +
                    '}';
        }

    }

}
