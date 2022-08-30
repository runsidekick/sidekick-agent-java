package com.runsidekick.agent.core.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.runsidekick.agent.core.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON mapper implementation which serialized/deserialize JSON data with type information.
 *
 * @author serkan
 */
public class TypeAwareJsonMapper {

    private final ThreadLocal<ObjectMapper> defaultObjectMapperThreadLocal =
            new ThreadLocal<ObjectMapper>() {
                @Override
                protected ObjectMapper initialValue() {
                    return new ObjectMapper().
                            configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false).
                            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).
                            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                }
            };
    private final ThreadLocal<ReaderWriterInfo> readerWriterInfoThreadLocal =
            new ThreadLocal<ReaderWriterInfo>() {
                @Override
                protected ReaderWriterInfo initialValue() {
                    return new ReaderWriterInfo();
                }
            };

    private static class ReaderWriterInfo {
        private final ObjectMapper om =
                new ObjectMapper().
                        configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false).
                        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).
                        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        private final Map<Class, ObjectReader> readerMap = new HashMap<Class, ObjectReader>();
        private final Map<Class, ObjectWriter> writerMap = new HashMap<Class, ObjectWriter>();
    }

    private ObjectReader getOrCreateReader(Class<?> clazz) {
        ReaderWriterInfo readerWriterInfo = readerWriterInfoThreadLocal.get();
        ObjectReader reader = readerWriterInfo.readerMap.get(clazz);
        if (reader == null) {
            reader = readerWriterInfo.om.readerFor(clazz);
        }
        return reader;
    }

    private ObjectWriter getOrCreateWriter(Class<?> clazz) {
        ReaderWriterInfo readerWriterInfo = readerWriterInfoThreadLocal.get();
        ObjectWriter writer = readerWriterInfo.writerMap.get(clazz);
        if (writer == null) {
            writer = readerWriterInfo.om.writerFor(clazz);
        }
        return writer;
    }

    public <T> Class<T> extractClass(String json) throws IOException, ClassNotFoundException {
        ReaderWriterInfo readerWriterInfo = readerWriterInfoThreadLocal.get();
        String className = readerWriterInfo.om.readTree(json).fields().next().getKey();
        return ClassUtils.getClassWithException(className);
    }

    public <T> Class<T> extractClass(byte[] data) throws IOException, ClassNotFoundException {
        ReaderWriterInfo readerWriterInfo = readerWriterInfoThreadLocal.get();
        String className = readerWriterInfo.om.readTree(data).fields().next().getKey();
        return ClassUtils.getClassWithException(className);
    }

    public <T> Class<T> extractClass(InputStream is) throws IOException, ClassNotFoundException {
        StringBuilder classNameBuilder = new StringBuilder(64);
        char c;
        boolean started = false;
        while (true) {
            c = (char) is.read();
            if (((short) c) < 0) {
                break;
            }
            if (c == '"') {
                if (started) {
                    is.read(); // Read ':' character
                    break;
                } else {
                    started = true;
                }
            } else {
                if (started) {
                    classNameBuilder.append(c);
                }
            }
        }
        String className = classNameBuilder.toString();
        return ClassUtils.getClassWithException(className);

        /*
        ReaderWriterInfo readerWriterInfo = readerWriterInfoThreadLocal.get();
        if (is.markSupported()) {
            is.mark(Integer.MAX_VALUE);
        }
        String className = readerWriterInfo.om.readTree(is).fields().next().getKey();
        if (is.markSupported()) {
            is.reset();
        }
        return ClassUtils.getClassWithException(className);
        */
    }

    public <T> T readObject(String json) throws IOException, ClassNotFoundException {
        Class<T> clazz = extractClass(json);
        return getOrCreateReader(clazz).
                withRootName(clazz.getName()).
                withFeatures(DeserializationFeature.UNWRAP_ROOT_VALUE).
                readValue(json);
    }

    public <T> T readObject(byte[] data) throws IOException, ClassNotFoundException {
        Class<T> clazz = extractClass(data);
        return getOrCreateReader(clazz).
                withRootName(clazz.getName()).
                withFeatures(DeserializationFeature.UNWRAP_ROOT_VALUE).
                readValue(data);
    }

    public <T> T readObject(InputStream is) throws IOException, ClassNotFoundException {
        Class<T> clazz = extractClass(is);
        return getOrCreateReader(clazz).readValue(is);
    }

    public <T> T readObject(byte[] data, Class<T> clazz) throws IOException {
        return getOrCreateReader(clazz).readValue(data);
    }

    public <T> T readObject(String json, Class<T> clazz) throws IOException {
        return getOrCreateReader(clazz).readValue(json);
    }

    public <T> T readObject(InputStream is, Class<T> clazz) throws IOException {
        return getOrCreateReader(clazz).readValue(is);
    }

    public <T> T readTypedObject(byte[] data, Class<T> clazz) throws IOException {
        return getOrCreateReader(clazz).
                withRootName(clazz.getName()).
                withFeatures(DeserializationFeature.UNWRAP_ROOT_VALUE).
                readValue(data);
    }

    public <T> T readTypedObject(String json, Class<T> clazz) throws IOException {
        return getOrCreateReader(clazz).
                withRootName(clazz.getName()).
                withFeatures(DeserializationFeature.UNWRAP_ROOT_VALUE).
                readValue(json);
    }

    public <T> T readTypedObject(InputStream is, Class<T> clazz) throws IOException {
        return getOrCreateReader(clazz).
                withRootName(clazz.getName()).
                withFeatures(DeserializationFeature.UNWRAP_ROOT_VALUE).
                readValue(is);
    }

    public String writeObject(Object obj) throws JsonProcessingException {
        if (obj != null) {
            Class<?> clazz = obj.getClass();
            return getOrCreateWriter(clazz).writeValueAsString(obj);
        } else {
            return defaultObjectMapperThreadLocal.get().writeValueAsString(null);
        }
    }

    public void writeObject(OutputStream os, Object obj) throws IOException {
        if (obj != null) {
            Class<?> clazz = obj.getClass();
            getOrCreateWriter(clazz).writeValue(os, obj);
        } else {
            defaultObjectMapperThreadLocal.get().writeValue(os, null);
        }
    }

    public String writeTypedObject(Object obj) throws JsonProcessingException {
        if (obj != null) {
            Class<?> clazz = obj.getClass();
            return getOrCreateWriter(clazz).withRootName(clazz.getName()).writeValueAsString(obj);
        } else {
            return defaultObjectMapperThreadLocal.get().writeValueAsString(null);
        }
    }

    public void writeTypedObject(OutputStream os, Object obj) throws IOException {
        if (obj != null) {
            Class<?> clazz = obj.getClass();
            getOrCreateWriter(clazz).withRootName(clazz.getName()).writeValue(os, obj);
        } else {
            defaultObjectMapperThreadLocal.get().writeValue(os, null);
        }
    }

}
