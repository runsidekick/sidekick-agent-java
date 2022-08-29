package com.runsidekick.agent.tracepoint.serialization;

import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.TextBuffer;

import java.io.Writer;

/**
 * @author serkan
 */
class JsonWriter extends Writer {

    private final TextBuffer buffer;

    JsonWriter(BufferRecycler br) {
        buffer = new TextBuffer(br);
    }

    @Override
    public Writer append(char c) {
        write(c);
        return this;
    }

    @Override
    public Writer append(CharSequence csq) {
        String str = csq.toString();
        buffer.append(str, 0, str.length());
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) {
        String str = csq.subSequence(start, end).toString();
        buffer.append(str, 0, str.length());
        return this;
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void write(char[] cbuf) {
        buffer.append(cbuf, 0, cbuf.length);
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        buffer.append(cbuf, off, len);
    }

    @Override
    public void write(int c) {
        buffer.append((char) c);
    }

    @Override
    public void write(String str) {
        buffer.append(str, 0, str.length());
    }

    @Override
    public void write(String str, int off, int len) {
        buffer.append(str, off, len);
    }

    int size() {
        return buffer.size();
    }

    String getAndClear() {
        String result = buffer.contentsAsString();
        buffer.releaseBuffers();
        return result;
    }

}
