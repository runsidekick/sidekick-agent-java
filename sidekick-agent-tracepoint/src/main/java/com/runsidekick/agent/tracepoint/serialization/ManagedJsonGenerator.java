package com.runsidekick.agent.tracepoint.serialization;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;
import com.runsidekick.agent.core.util.PropertyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * @author serkan
 */
class ManagedJsonGenerator extends WriterBasedJsonGenerator {

    private static final int MAX_SERIALIZED_TEXT_LENGTH =
            PropertyUtils.getIntegerProperty(
                    "sidekick.agent.tracepoint.serialization.text.maxlength",
                    100);
    private static final int MAX_SERIALIZED_BYTEBUFFER_LENGTH =
            PropertyUtils.getIntegerProperty(
                    "sidekick.agent.tracepoint.serialization.binary.maxlength",
                    100);

    ManagedJsonGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w) {
        super(ctxt, features, codec, w);
        this._writeContext = new ManagedJsonWriteContext(this._writeContext);
    }

    @Override
    public void writeFieldName(String name) throws IOException {
        JsonWriteContext jwc = (JsonWriteContext) super.getOutputContext();
        if (jwc instanceof ManagedJsonWriteContext) {
            ManagedJsonWriteContext mjwc = (ManagedJsonWriteContext) jwc;
            name = mjwc.checkAndGetFieldName(name);
            super.writeFieldName(name);
        } else {
            super.writeFieldName(name);
        }
    }

    @Override
    public void writeFieldName(SerializableString serializableString) throws IOException {
        // TODO Check and handle duplicate field names
        super.writeFieldName(serializableString);
    }

    @Override
    public void writeString(String text) throws IOException {
        // TODO Check and trim string if bigger than limit
        super.writeString(text);
    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException {
        super.writeString(text, offset, Math.min(MAX_SERIALIZED_TEXT_LENGTH, len));
    }

    @Override
    public void writeString(SerializableString serializableString) throws IOException {
        // TODO Check and trim string if bigger than limit
        super.writeString(serializableString);
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int len) throws IOException {
        super.writeRawUTF8String(text, offset, Math.min(MAX_SERIALIZED_TEXT_LENGTH, len));
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int len) throws IOException {
        super.writeUTF8String(text, offset, Math.min(MAX_SERIALIZED_TEXT_LENGTH, len));
    }

    @Override
    public void writeRaw(String text) throws IOException {
        // TODO Check and trim string if bigger than limit
        super.writeRaw(text);
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException {
        super.writeRaw(text, offset, Math.min(MAX_SERIALIZED_TEXT_LENGTH, len));
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException {
        super.writeRaw(text, offset, Math.min(MAX_SERIALIZED_TEXT_LENGTH, len));
    }

    @Override
    public void writeRawValue(String text) throws IOException {
        // TODO Check and trim string if bigger than limit
        super.writeRawValue(text);
    }

    @Override
    public void writeRawValue(String text, int offset, int len) throws IOException {
        super.writeRawValue(text, offset, Math.min(MAX_SERIALIZED_TEXT_LENGTH, len));
    }

    @Override
    public void writeRawValue(char[] text, int offset, int len) throws IOException {
        super.writeRawValue(text, offset, Math.min(MAX_SERIALIZED_TEXT_LENGTH, len));
    }

    @Override
    public void writeBinary(Base64Variant base64Variant, byte[] data, int offset, int len) throws IOException {
        super.writeBinary(base64Variant, data, offset, Math.min(MAX_SERIALIZED_BYTEBUFFER_LENGTH, len));
    }

    @Override
    public int writeBinary(Base64Variant base64Variant, InputStream data, int len) throws IOException {
        return super.writeBinary(base64Variant, data, Math.min(MAX_SERIALIZED_BYTEBUFFER_LENGTH, len));
    }

}
