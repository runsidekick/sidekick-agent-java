package com.runsidekick.agent.tracepoint.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.runsidekick.agent.probe.domain.Variable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Holds variables with their names, types and values.
 *
 * @author serkan
 */
@JsonSerialize(using = Variables.VariablesSerializer.class)
public class Variables {

    private final List<Variable> variables;
    private final Map<String, String> serializedVariables;

    public Variables(List<Variable> variables) {
        this.variables = variables;
        this.serializedVariables = null;
    }

    public Variables(Map<String, String> serializedVariables) {
        this.serializedVariables = serializedVariables;
        this.variables = null;
    }

    public static class VariablesSerializer extends StdSerializer<Variables> {

        protected VariablesSerializer() {
            super(Variables.class);
        }

        @Override
        public void serialize(Variables variables, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (variables.serializedVariables != null) {
                gen.writeStartObject();
                for (Map.Entry<String, String> e : variables.serializedVariables.entrySet()) {
                    gen.writeFieldName(e.getKey());
                    gen.writeRawValue(e.getValue());
                }
                gen.writeEndObject();
            } else {
                gen.writeStartObject();
                for (Variable variable : variables.variables) {
                    gen.writeFieldName(variable.getName());
                    Variable.VariableSerializer.serializeValue(gen, variable);
                }
                gen.writeEndObject();
            }
        }

    }

    @Override
    public String toString() {
        return "Variables{" +
                "variables=" + variables +
                '}';
    }

}
