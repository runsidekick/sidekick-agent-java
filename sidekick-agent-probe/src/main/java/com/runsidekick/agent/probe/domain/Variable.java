package com.runsidekick.agent.probe.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.runsidekick.agent.api.dataredaction.DataRedactionContext;
import com.runsidekick.agent.dataredaction.DataRedactionHelper;
import com.runsidekick.agent.serialization.SerializationHelper;

import java.io.IOException;

/**
 * Holds variable with its name, type and value.
 *
 * @author serkan
 */
@JsonSerialize(using = Variable.VariableSerializer.class)
public class Variable {

    final String name;
    final String type;
    final Object value;

    public Variable(String name, Object value) {
        this.name = name;
        this.type = value == null ? null : value.getClass().getName();
        this.value = value;
    }

    public Variable(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                '}';
    }

    public static class VariableSerializer extends StdSerializer<Variable> {

        public VariableSerializer() {
            super(Variable.class);
        }

        public static String serializeVariable(Variable variable) {
            return serializeVariable(variable, null);
        }

        public static String serializeVariable(Variable variable, DataRedactionContext dataRedactionContext) {
            String serializedValue;
            try {
                boolean skipValueFromSerialization =
                        SerializationHelper.shouldSkipValueFromSerialization(variable.value);
                if (skipValueFromSerialization) {
                    serializedValue = SerializationHelper.wrapAsIgnored(
                            SerializationHelper.SKIPPED_VALUE_SERIALIZATION_MESSAGE);
                } else {
                    if (DataRedactionHelper.shouldRedactVariable(dataRedactionContext, variable.name)) {
                        serializedValue = SerializationHelper.wrapAsRedacted();
                    } else {
                        serializedValue = SerializationHelper.serializeValue(variable.value, dataRedactionContext);
                    }
                }
            } catch (Throwable t) {
                String serializationErrorMessage = SerializationHelper.generateSerializationErrorMessage(t);
                serializedValue = SerializationHelper.wrapAsErroneous(serializationErrorMessage);
            }
            return serializedValue;
        }

        public static void serializeValue(JsonGenerator gen, Variable variable) throws IOException {
            String serializedValue = serializeVariable(variable);
            gen.writeRawValue(serializedValue);
        }

        @Override
        public void serialize(Variable variable, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();

            gen.writeStringField("name", variable.name);
            gen.writeStringField("type", variable.type);

            gen.writeFieldName("value");
            serializeValue(gen, variable);

            gen.writeEndObject();
        }

    }

}
