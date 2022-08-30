package com.runsidekick.agent.tracepoint.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import java.io.IOException;
import java.io.Writer;

/**
 * @author serkan
 */
public class ManagedJsonFactory extends MappingJsonFactory {

    @Override
    protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
        ManagedJsonGenerator gen =
                new ManagedJsonGenerator(ctxt, _generatorFeatures, _objectCodec, out);
        if (_characterEscapes != null) {
            gen.setCharacterEscapes(_characterEscapes);
        }
        SerializableString rootSep = _rootValueSeparator;
        if (rootSep != DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR) {
            gen.setRootValueSeparator(rootSep);
        }
        return gen;
    }

}
