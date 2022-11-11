package com.runsidekick.agent.probe.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.runsidekick.agent.api.dataredaction.DataRedactionContext;
import com.runsidekick.agent.core.config.ConfigProvider;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.PropertyUtils;
import com.runsidekick.agent.dataredaction.DataRedactionHelper;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author serkan
 */
public final class SerializationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializationHelper.class);

    private static final String MAX_SERIALIZED_DATA_SIZE_PROP_NAME = "sidekick.agent.tracepoint.stacktrace.maxdepth";
    private static final String MAX_DEPTH_ON_SERIALIZATION_PROP_NAME = "sidekick.agent.tracepoint.serialization.depth.max";
    private static final String MAX_SERIALIZED_ARRAY_LENGTH_PROP_NAME = "sidekick.agent.tracepoint.serialization.array.length.max";

    private static final int MAX_SERIALIZED_DATA_SIZE =
            PropertyUtils.getIntegerProperty(
                    MAX_SERIALIZED_DATA_SIZE_PROP_NAME,
                    64 * 1024); // 64 KB by default
    private static final int MAX_DEPTH_ON_SERIALIZATION =
            PropertyUtils.getIntegerProperty(
                    MAX_DEPTH_ON_SERIALIZATION_PROP_NAME,
                    3);
    private static final int MAX_SERIALIZED_ARRAY_LENGTH =
            PropertyUtils.getIntegerProperty(
                    MAX_SERIALIZED_ARRAY_LENGTH_PROP_NAME,
                    100);

    private static final boolean OMIT_WHEN_IGNORED = true;

    public static final int MAX_SERIALIZATION_ERROR_MESSAGE_LENGTH = 100;
    public static final String SKIPPED_VALUE_SERIALIZATION_MESSAGE = "<Skipped>";

    public static final String TYPE_PROPERTY = "@type";
    public static final String VALUE_PROPERTY = "@value";
    public static final String ARRAY_PROPERTY = "@array";
    public static final String ARRAY_ELEMENT_TYPE_PROPERTY = "@array-element-type";
    public static final String ENUM_PROPERTY = "@enum";
    public static final String MAP_PROPERTY = "@map";
    public static final String IGNORED_PROPERTY = "@ignored";
    public static final String IGNORED_REASON_PROPERTY = "@ignored-reason";
    public static final String ERRONEOUS_PROPERTY = "@erroneous";
    public static final String DATA_REDACTED_PROPERTY = "@data-redacted";
    public static final Set<Class> IGNORED_TYPES = new HashSet<Class>() {{
        add(InputStream.class);
        add(OutputStream.class);
        add(Connection.class);
    }};

    private static final ThreadLocal<ObjectMapper> threadLocalObjectMapper =
            ThreadLocal.withInitial(() -> createObjectMapper());

    private static final ThreadLocal<JsonWriter> threadLocalJsonWriter = new ThreadLocal<>();

    private SerializationHelper() {
    }

    private static ObjectMapper createObjectMapper() {
        SimpleModule module = new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addBeanSerializerModifier(new DepthAwareBeanSerializerModifier());
            }
        };
        module.addSerializer(boolean.class, new BooleanSerializer(true));
        module.addSerializer(Boolean.class, new BooleanSerializer(false));
        module.addSerializer(byte.class, new ByteSerializer(true));
        module.addSerializer(Byte.class, new ByteSerializer(false));
        module.addSerializer(short.class, new ShortSerializer(true));
        module.addSerializer(Short.class, new ShortSerializer(false));
        module.addSerializer(int.class, new IntegerSerializer(true));
        module.addSerializer(Integer.class, new IntegerSerializer(false));
        module.addSerializer(float.class, new FloatSerializer(true));
        module.addSerializer(Float.class, new FloatSerializer(false));
        module.addSerializer(long.class, new LongSerializer(true));
        module.addSerializer(Long.class, new LongSerializer(false));
        module.addSerializer(double.class, new DoubleSerializer(true));
        module.addSerializer(Double.class, new DoubleSerializer(false));
        module.addSerializer(byte[].class, new ByteArraySerializer());

        ObjectMapper objectMapper = new ObjectMapper(new ManagedJsonFactory());

        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.getSerializerProvider().setNullKeySerializer(new NullKeySerializer());
        objectMapper.getSerializerProvider().setNullValueSerializer(new TypeAwareNullSerializer());

        objectMapper.setFilterProvider(
                new SimpleFilterProvider().
                        addFilter(DepthAwarePropertyFilter.FILTER_ID,
                                new DepthAwarePropertyFilter(
                                        ConfigProvider.getIntegerProperty(MAX_DEPTH_ON_SERIALIZATION_PROP_NAME, MAX_DEPTH_ON_SERIALIZATION))));
        objectMapper.registerModule(module);
        objectMapper.registerModule(new AfterburnerModule());
        return objectMapper;
    }

    private static class SerializationContext {

        private static final ThreadLocal<SerializationContext> threadLocalSerializationContext =
                new ThreadLocal<>();

        private final SerializationContext parentSerializationContext;
        private boolean disableTypeInjecting;
        private Class serializingFieldType;
        private DataRedactionContext dataRedactionContext;

        private SerializationContext(SerializationContext parentSerializationContext) {
            this.parentSerializationContext = parentSerializationContext;
        }

        private SerializationContext(SerializationContext parentSerializationContext,
                                     boolean disableTypeInjecting, Class serializingFieldType) {
            this.parentSerializationContext = parentSerializationContext;
            this.disableTypeInjecting = disableTypeInjecting;
            this.serializingFieldType = serializingFieldType;
        }

        private static SerializationContext get() {
            return threadLocalSerializationContext.get();
        }

        private static SerializationContext start() {
            SerializationContext currentSerializationContext = threadLocalSerializationContext.get();
            SerializationContext newSerializationContext = new SerializationContext(currentSerializationContext);
            threadLocalSerializationContext.set(newSerializationContext);
            return newSerializationContext;
        }

        private void close() {
            if (parentSerializationContext == null) {
                threadLocalSerializationContext.remove();
            } else {
                threadLocalSerializationContext.set(parentSerializationContext);
            }
        }

        private static void destroy() {
            threadLocalSerializationContext.remove();
        }

        public DataRedactionContext getDataRedactionContext() {
            if (dataRedactionContext == null) {
                if (parentSerializationContext != null) {
                    return parentSerializationContext.getDataRedactionContext();
                }
            }
            return dataRedactionContext;
        }

    }

    private static class NullKeySerializer extends StdSerializer<Object> {

        public NullKeySerializer() {
            super((Class) null);
        }

        @Override
        public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused)
                throws IOException {
            jsonGenerator.writeFieldName("");
        }

    }

    private static class TypeAwareNullSerializer extends StdSerializer<Object> {

        private TypeAwareNullSerializer() {
            super(Object.class);
        }

        private void serializeValue(JsonGenerator gen) throws IOException {
            SerializationContext serializationContext = SerializationContext.get();
            if (serializationContext == null || serializationContext.serializingFieldType == null) {
                gen.writeNull();
                return;
            }
            gen.writeStartObject();
            gen.writeFieldName(TYPE_PROPERTY);
            gen.writeString(getTypeName(serializationContext.serializingFieldType));
            gen.writeFieldName(VALUE_PROPERTY);
            gen.writeNull();
            gen.writeEndObject();
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            serializeValue(gen);
        }

        @Override
        public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers,
                                      TypeSerializer typeSer) throws IOException {
            serializeValue(gen);
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
            return createSchemaNode("null");
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitor.expectNullFormat(typeHint);
        }

    }

    private static class TypeAwareSerializerWrapper
            extends JsonSerializer
            implements ResolvableSerializer {

        private final JsonSerializer originalSerializer;
        private final boolean typeAwareSerializer;

        private TypeAwareSerializerWrapper(JsonSerializer originalSerializer) {
            this.originalSerializer = originalSerializer;
            this.typeAwareSerializer =
                    (originalSerializer instanceof TypeAwareSerializer)
                            || (originalSerializer instanceof TypeAwareSerializerWrapper);
        }

        private boolean startSerialize(Object value, JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            gen.writeFieldName(TYPE_PROPERTY);
            gen.writeString(getTypeName(value));
            boolean writeEmptyArray = false, isArray = false, shouldRedactVariable = shouldRedactVariable(gen);
            if (value != null) {
                Class valueClass = value.getClass();
                if (valueClass.isArray()) {
                    isArray = true;
                    gen.writeFieldName(ARRAY_PROPERTY);
                    gen.writeBoolean(true);
                    int length = Array.getLength(value);
                    Class elementType = valueClass.getComponentType();
                    if (isPrimitiveType(elementType) || Modifier.isFinal(elementType.getModifiers())) {
                        gen.writeFieldName(ARRAY_ELEMENT_TYPE_PROPERTY);
                        gen.writeString(getTypeName(valueClass.getComponentType()));
                    }
                    if (length > ConfigProvider.getIntegerProperty(MAX_SERIALIZED_ARRAY_LENGTH_PROP_NAME, MAX_SERIALIZED_ARRAY_LENGTH)) {
                        gen.writeFieldName(IGNORED_PROPERTY);
                        gen.writeBoolean(true);
                        gen.writeFieldName(IGNORED_REASON_PROPERTY);
                        gen.writeString(String.format(
                                "Array size %d is bigger than max array size %d",
                                length, ConfigProvider.getIntegerProperty(MAX_SERIALIZED_ARRAY_LENGTH_PROP_NAME, MAX_SERIALIZED_ARRAY_LENGTH)));
                        writeEmptyArray = true;
                    }
                } else if (valueClass.isEnum()) {
                    gen.writeFieldName(ENUM_PROPERTY);
                    gen.writeBoolean(true);
                }
            }
            gen.writeFieldName(VALUE_PROPERTY);
            if (shouldRedactVariable) {
                if (isArray) {
                    writeEmptyArray(gen);
                } else {
                    gen.writeNull();
                }
                wrapAsRedacted(gen);
                return false;
            }
            if (writeEmptyArray) {
                writeEmptyArray(gen);
                return false;
            }
            return true;
        }

        private void endSerialize(JsonGenerator gen) throws IOException {
            gen.writeEndObject();
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (typeAwareSerializer) {
                originalSerializer.serialize(value, gen, serializers);
                return;
            }
            boolean serialize = startSerialize(value, gen);
            if (serialize) {
                originalSerializer.serialize(value, gen, serializers);
            }
            endSerialize(gen);
        }

        @Override
        public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers,
                                      TypeSerializer typeSer) throws IOException {
            startSerialize(value, gen);
            super.serializeWithType(value, gen, serializers, typeSer);
            endSerialize(gen);
        }

        @Override
        public boolean isEmpty(Object value) {
            return originalSerializer.isEmpty(value);
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, Object value) {
            return originalSerializer.isEmpty(provider, value);
        }

        @Override
        public JsonSerializer unwrappingSerializer(NameTransformer unwrapper) {
            return originalSerializer.unwrappingSerializer(unwrapper);
        }

        @Override
        public JsonSerializer replaceDelegatee(JsonSerializer delegatee) {
            return originalSerializer.replaceDelegatee(delegatee);
        }

        @Override
        public JsonSerializer<?> withFilterId(Object filterId) {
            return originalSerializer.withFilterId(filterId);
        }

        @Override
        public Class handledType() {
            return originalSerializer.handledType();
        }

        @Override
        public boolean usesObjectId() {
            return originalSerializer.usesObjectId();
        }

        @Override
        public boolean isUnwrappingSerializer() {
            return originalSerializer.isUnwrappingSerializer();
        }

        @Override
        public JsonSerializer<?> getDelegatee() {
            return originalSerializer.getDelegatee();
        }

        @Override
        public Iterator<PropertyWriter> properties() {
            return originalSerializer.properties();
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
            originalSerializer.acceptJsonFormatVisitor(visitor, type);
        }

        @Override
        public void resolve(SerializerProvider provider) throws JsonMappingException {
            if (originalSerializer instanceof ResolvableSerializer) {
                ((ResolvableSerializer) originalSerializer).resolve(provider);
            }
        }

    }

    private static class TypeAwareMapSerializerWrapper extends MapSerializer {

        private final ThreadLocal<Boolean> threadLocalInProgress = new ThreadLocal<>();

        private TypeAwareMapSerializerWrapper(MapSerializer originalSerializer) {
            super(originalSerializer, getValueTypeSerializer(originalSerializer), null, false);
        }

        public TypeAwareMapSerializerWrapper(Set<String> ignoredEntries, JavaType keyType, JavaType valueType,
                                             boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<?> keySerializer,
                                             JsonSerializer<?> valueSerializer) {
            super(ignoredEntries, keyType, valueType, valueTypeIsStatic, vts, keySerializer, valueSerializer);
        }

        public TypeAwareMapSerializerWrapper(MapSerializer src, BeanProperty property, JsonSerializer<?> keySerializer,
                                             JsonSerializer<?> valueSerializer, Set<String> ignoredEntries) {
            super(src, property, keySerializer, valueSerializer, ignoredEntries);
        }

        public TypeAwareMapSerializerWrapper(MapSerializer src, TypeSerializer vts, Object suppressableValue, boolean suppressNulls) {
            super(src, vts, suppressableValue, suppressNulls);
        }

        public TypeAwareMapSerializerWrapper(MapSerializer src, Object filterId, boolean sortKeys) {
            super(src, filterId, sortKeys);
        }

        public TypeAwareMapSerializerWrapper(MapSerializer src, TypeSerializer vts, Object suppressableValue) {
            super(src, vts, suppressableValue);
        }

        private static TypeSerializer getValueTypeSerializer(MapSerializer mapSerializer) {
            try {
                Field valueTypeSerializerField = MapSerializer.class.getDeclaredField("_valueTypeSerializer");
                valueTypeSerializerField.setAccessible(true);
                return (TypeSerializer) valueTypeSerializerField.get(mapSerializer);
            } catch (Exception e) {
                return null;
            }
        }

        private void startSerialize(Map value, JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            gen.writeFieldName(TYPE_PROPERTY);
            gen.writeString(getTypeName(value));
            gen.writeFieldName(MAP_PROPERTY);
            gen.writeBoolean(true);
            gen.writeFieldName(VALUE_PROPERTY);
        }

        private void endSerialize(JsonGenerator gen) throws IOException {
            gen.writeEndObject();
        }

        @Override
        public void serialize(Map value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (Boolean.TRUE.equals(threadLocalInProgress.get())) {
                super.serialize(value, gen, serializers);
                return;
            }
            try {
                threadLocalInProgress.set(true);
                startSerialize(value, gen);
                super.serialize(value, gen, serializers);
                endSerialize(gen);
            } finally {
                threadLocalInProgress.remove();
            }
        }

        @Override
        public void serializeFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (Boolean.TRUE.equals(threadLocalInProgress.get())) {
                super.serializeFields(value, gen, provider);
                return;
            }
            try {
                threadLocalInProgress.set(true);
                startSerialize(value, gen);
                super.serializeFields(value, gen, provider);
                endSerialize(gen);
            } finally {
                threadLocalInProgress.remove();
            }
        }

        @Override
        public void serializeWithType(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider,
                                      TypeSerializer typeSer) throws IOException {
            if (Boolean.TRUE.equals(threadLocalInProgress.get())) {
                super.serializeWithType(value, gen, provider, typeSer);
                return;
            }
            try {
                threadLocalInProgress.set(true);
                startSerialize(value, gen);
                super.serializeWithType(value, gen, provider, typeSer);
                endSerialize(gen);
            } finally {
                threadLocalInProgress.remove();
            }
        }

        @Override
        public void serializeOptionalFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider,
                                            Object suppressableValue) throws IOException {
            if (Boolean.TRUE.equals(threadLocalInProgress.get())) {
                super.serializeOptionalFields(value, gen, provider, suppressableValue);
                return;
            }
            try {
                threadLocalInProgress.set(true);
                startSerialize(value, gen);
                super.serializeOptionalFields(value, gen, provider, suppressableValue);
                endSerialize(gen);
            } finally {
                threadLocalInProgress.remove();
            }
        }

        @Override
        public void serializeFieldsUsing(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider,
                                         JsonSerializer<Object> ser) throws IOException {
            if (Boolean.TRUE.equals(threadLocalInProgress.get())) {
                super.serializeFieldsUsing(value, gen, provider, ser);
                return;
            }
            try {
                threadLocalInProgress.set(true);
                startSerialize(value, gen);
                super.serializeFieldsUsing(value, gen, provider, ser);
                endSerialize(gen);
            } finally {
                threadLocalInProgress.remove();
            }
        }

        @Override
        public void serializeFilteredFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider,
                                            PropertyFilter filter, Object suppressableValue) throws IOException {
            if (Boolean.TRUE.equals(threadLocalInProgress.get())) {
                super.serializeFilteredFields(value, gen, provider, filter, suppressableValue);
                return;
            }
            try {
                threadLocalInProgress.set(true);
                startSerialize(value, gen);
                super.serializeFilteredFields(value, gen, provider, filter, suppressableValue);
                endSerialize(gen);
            } finally {
                threadLocalInProgress.remove();
            }
        }

        @Override
        public void serializeTypedFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider,
                                         Object suppressableValue) throws IOException {
            if (Boolean.TRUE.equals(threadLocalInProgress.get())) {
                super.serializeTypedFields(value, gen, provider, suppressableValue);
                return;
            }
            try {
                threadLocalInProgress.set(true);
                startSerialize(value, gen);
                super.serializeTypedFields(value, gen, provider, suppressableValue);
                endSerialize(gen);
            } finally {
                threadLocalInProgress.remove();
            }
        }

        @Override
        protected void _ensureOverride() {
        }

        @Override
        public MapSerializer _withValueTypeSerializer(TypeSerializer vts) {
            if (_valueTypeSerializer == vts) {
                return this;
            }
            _ensureOverride();
            return new TypeAwareMapSerializerWrapper(this, vts, null);
        }

        @Override
        public MapSerializer withResolved(BeanProperty property,
                                          JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer,
                                          Set<String> ignored, boolean sortKeys) {
            _ensureOverride();
            TypeAwareMapSerializerWrapper ser =
                    new TypeAwareMapSerializerWrapper(this, property, keySerializer, valueSerializer, ignored);
            if (sortKeys != ser._sortKeys) {
                ser = new TypeAwareMapSerializerWrapper(ser, _filterId, sortKeys);
            }
            return ser;
        }

        @Override
        public MapSerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer,
                                          JsonSerializer<?> valueSerializer, Set<String> ignored,
                                          Set<String> included, boolean sortKeys) {
            return this.withResolved(property, keySerializer, valueSerializer, ignored, sortKeys);
        }

        @Override
        public MapSerializer withFilterId(Object filterId) {
            if (_filterId == filterId) {
                return this;
            }
            _ensureOverride();
            return new TypeAwareMapSerializerWrapper(this, filterId, _sortKeys);
        }

        @Override
        public MapSerializer withContentInclusion(Object suppressableValue) {
            if (suppressableValue == _suppressableValue) {
                return this;
            }
            _ensureOverride();
            return new TypeAwareMapSerializerWrapper(this, _valueTypeSerializer, suppressableValue);
        }

    }

    private static abstract class TypeAwareSerializer<T> extends JsonSerializer<T> {

        private final boolean forPrimitive;
        private final Class<T> type;
        private final String primitiveTypeName;

        protected TypeAwareSerializer(boolean forPrimitive, Class<T> type, String primitiveTypeName) {
            this.forPrimitive = forPrimitive;
            this.type = type;
            this.primitiveTypeName = primitiveTypeName;
        }

        @Override
        public void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            SerializationContext serializationContext = SerializationContext.get();
            if (serializationContext != null && serializationContext.disableTypeInjecting) {
                serializeValue(value, jgen);
                return;
            }
            jgen.writeStartObject();
            jgen.writeFieldName(TYPE_PROPERTY);
            jgen.writeString(forPrimitive ? primitiveTypeName : getTypeName(value));
            beforeSerializeValue(value, jgen);
            jgen.writeFieldName(VALUE_PROPERTY);
            serializeValue(value, jgen);
            afterSerializeValue(value, jgen);
            jgen.writeEndObject();
        }

        @Override
        public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            this.serialize(value, gen, serializers);
        }

        protected void beforeSerializeValue(T value, JsonGenerator jgen) throws IOException {
        }

        protected abstract void serializeValue(T value, JsonGenerator jgen) throws IOException;

        protected void afterSerializeValue(T value, JsonGenerator jgen) throws IOException {
        }

    }

    private static class BooleanSerializer extends TypeAwareSerializer<Boolean> {

        private BooleanSerializer(boolean forPrimitive) {
            super(forPrimitive, Boolean.class, Boolean.TYPE.getName());
        }

        @Override
        protected void serializeValue(Boolean value, JsonGenerator jgen) throws IOException {
            if (shouldRedactVariable(jgen)) {
                jgen.writeNull();
                wrapAsRedacted(jgen);
            } else {
                jgen.writeBoolean(value);
            }
        }

    }

    private static class ByteSerializer extends TypeAwareSerializer<Byte> {

        private ByteSerializer(boolean forPrimitive) {
            super(forPrimitive, Byte.class, Byte.TYPE.getName());
        }

        @Override
        protected void serializeValue(Byte value, JsonGenerator jgen) throws IOException {
            if (shouldRedactVariable(jgen)) {
                jgen.writeNull();
                wrapAsRedacted(jgen);
            } else {
                jgen.writeNumber(value);
            }
        }

    }

    private static class ShortSerializer extends TypeAwareSerializer<Short> {

        private ShortSerializer(boolean forPrimitive) {
            super(forPrimitive, Short.class, Short.TYPE.getName());
        }

        @Override
        protected void serializeValue(Short value, JsonGenerator jgen) throws IOException {
            if (shouldRedactVariable(jgen)) {
                jgen.writeNull();
                wrapAsRedacted(jgen);
            } else {
                jgen.writeNumber(value);
            }
        }

    }

    private static class IntegerSerializer extends TypeAwareSerializer<Integer> {

        private IntegerSerializer(boolean forPrimitive) {
            super(forPrimitive, Integer.class, Integer.TYPE.getName());
        }

        @Override
        protected void serializeValue(Integer value, JsonGenerator jgen) throws IOException {
            if (shouldRedactVariable(jgen)) {
                jgen.writeNull();
                wrapAsRedacted(jgen);
            } else {
                jgen.writeNumber(value);
            }
        }

    }

    private static class FloatSerializer extends TypeAwareSerializer<Float> {

        private FloatSerializer(boolean forPrimitive) {
            super(forPrimitive, Float.class, Float.TYPE.getName());
        }

        @Override
        protected void serializeValue(Float value, JsonGenerator jgen) throws IOException {
            if (shouldRedactVariable(jgen)) {
                jgen.writeNull();
                wrapAsRedacted(jgen);
            } else {
                jgen.writeNumber(value);
            }
        }

    }

    private static class LongSerializer extends TypeAwareSerializer<Long> {

        private LongSerializer(boolean forPrimitive) {
            super(forPrimitive, Long.class, Long.TYPE.getName());
        }

        @Override
        protected void serializeValue(Long value, JsonGenerator jgen) throws IOException {
            if (shouldRedactVariable(jgen)) {
                jgen.writeNull();
                wrapAsRedacted(jgen);
            } else {
                jgen.writeNumber(value);
            }
        }

    }

    private static class DoubleSerializer extends TypeAwareSerializer<Double> {

        private DoubleSerializer(boolean forPrimitive) {
            super(forPrimitive, Double.class, Double.TYPE.getName());
        }

        @Override
        protected void serializeValue(Double value, JsonGenerator jgen) throws IOException {
            if (shouldRedactVariable(jgen)) {
                jgen.writeNull();
                wrapAsRedacted(jgen);
            } else {
                jgen.writeNumber(value);
            }
        }

    }

    private static class ByteArraySerializer extends TypeAwareSerializer<byte[]> {

        private ByteArraySerializer() {
            super(false, byte[].class, null);
        }

        @Override
        protected void beforeSerializeValue(byte[] value, JsonGenerator jgen) throws IOException {
            jgen.writeFieldName(ARRAY_PROPERTY);
            jgen.writeBoolean(true);
            jgen.writeFieldName(ARRAY_ELEMENT_TYPE_PROPERTY);
            jgen.writeString(byte.class.getName());
            if (value.length > ConfigProvider.getIntegerProperty(MAX_SERIALIZED_ARRAY_LENGTH_PROP_NAME, MAX_SERIALIZED_ARRAY_LENGTH)) {
                jgen.writeFieldName(IGNORED_PROPERTY);
                jgen.writeBoolean(true);
                jgen.writeFieldName(IGNORED_REASON_PROPERTY);
                jgen.writeString(String.format(
                        "Array size %d is bigger than max array size %d",
                        value.length, ConfigProvider.getIntegerProperty(MAX_SERIALIZED_ARRAY_LENGTH_PROP_NAME, MAX_SERIALIZED_ARRAY_LENGTH)));
            }
        }

        @Override
        protected void serializeValue(byte[] value, JsonGenerator jgen) throws IOException {
            if (shouldRedactVariable(jgen)) {
                writeEmptyArray(jgen);
                wrapAsRedacted(jgen);
            } else {
                if (value.length > ConfigProvider.getIntegerProperty(MAX_SERIALIZED_ARRAY_LENGTH_PROP_NAME, MAX_SERIALIZED_ARRAY_LENGTH)) {
                    writeEmptyArray(jgen);
                } else {
                    jgen.writeStartArray(value.length);
                    for (int i = 0; i < value.length; i++) {
                        jgen.writeNumber(value[i]);
                    }
                    jgen.writeEndArray();
                }
            }
        }

    }

    private static class IgnoredSerializer extends StdSerializer {

        private IgnoredSerializer() {
            super(Object.class);
        }

        private void serializeIgnored(Object value, JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            gen.writeFieldName(TYPE_PROPERTY);
            gen.writeString(getTypeName(value));
            gen.writeFieldName(IGNORED_PROPERTY);
            gen.writeBoolean(true);
            gen.writeFieldName(IGNORED_REASON_PROPERTY);
            gen.writeString("Ignored because of type");
            gen.writeEndObject();
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            serializeIgnored(value, gen);
        }

        @Override
        public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers,
                                      TypeSerializer typeSer) throws IOException
        {
            serializeIgnored(value, gen);
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
            return createSchemaNode("null");
        }

        @Override
        public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
            visitor.expectNullFormat(typeHint);
        }

    }

    private static class DepthAwarePropertyFilter extends SimpleBeanPropertyFilter {

        private static final String FILTER_ID = "SIDEKICK::DepthAwarePropertyFilter";

        private final int originalMaxDepth;
        private final int normalizedMaxDepth;

        private DepthAwarePropertyFilter(int maxDepth) {
            this.originalMaxDepth = maxDepth;
            // We are doubling depth here because we are wrapping properties to inject type info
            this.normalizedMaxDepth = 2 * maxDepth;
        }

        private int calculateDepth(PropertyWriter writer, JsonGenerator jgen) {
            JsonStreamContext sc = jgen.getOutputContext();
            int depth = -1;
            while (sc != null) {
                sc = sc.getParent();
                depth++;
            }
            return depth;
        }

        @Override
        public void serializeAsField(Object pojo, JsonGenerator gen, SerializerProvider provider,
                                     PropertyWriter writer) throws Exception {
            int currentSerializedDataSize = getCurrentSerializedDataSize();
            if (currentSerializedDataSize >= MAX_SERIALIZED_DATA_SIZE) {
                if (OMIT_WHEN_IGNORED) {
                    gen.writeOmittedField(writer.getName());
                } else {
                    wrapAsIgnored(gen, writer.getName(),
                            String.format("Ignored because current serialized data size (%d bytes) exceeded max allowed size (%d bytes)",
                                    currentSerializedDataSize, MAX_SERIALIZED_DATA_SIZE));
                }
                return;
            }
            int depth = calculateDepth(writer, gen);
            if (depth <= normalizedMaxDepth) {
                try {
                    Class fieldType = writer.getType().getRawClass();
                    if (isPrimitiveType(fieldType)) {
                        writePrimitiveType(fieldType, writer.getName(), pojo, gen, provider, writer);
                    } else {
                        SerializationContext serializationContext = SerializationContext.start();
                        serializationContext.serializingFieldType = fieldType;
                        try {
                            super.serializeAsField(pojo, gen, provider, writer);
                        } finally {
                            serializationContext.close();
                        }
                    }
                } catch (Throwable t) {
                    LOGGER.error("Error occurred while serializing field: " + writer.getName(), t);
                    wrapAsErroneous(gen, writer.getName(), t.getMessage());
                }
            } else {
                if (OMIT_WHEN_IGNORED) {
                    gen.writeOmittedField(writer.getName());
                } else {
                    wrapAsIgnored(gen, writer.getName(), "Ignored due to reached max depth " + originalMaxDepth);
                }
            }
        }

        private void writePrimitiveType(Class type, String fieldName,
                                        Object pojo, JsonGenerator gen,
                                        SerializerProvider provider,
                                        PropertyWriter writer) throws Exception {
            SerializationContext serializationContext = SerializationContext.start();
            serializationContext.disableTypeInjecting = true;
            try {
                gen.writeFieldName(fieldName);
                gen.writeStartObject();
                gen.writeFieldName(TYPE_PROPERTY);
                gen.writeString(type.getName());
                gen.writeFieldName(VALUE_PROPERTY);
                writer.serializeAsElement(pojo, gen, provider);
                gen.writeEndObject();
            } finally {
                serializationContext.close();
            }
        }

        @Override
        public void serializeAsElement(Object pojo, JsonGenerator gen, SerializerProvider provider,
                                       PropertyWriter writer) throws Exception {
            if (getCurrentSerializedDataSize() >= MAX_SERIALIZED_DATA_SIZE) {
                return;
            }
            int depth = calculateDepth(writer, gen);
            if (depth <= normalizedMaxDepth) {
                super.serializeAsElement(pojo, gen, provider, writer);
            }
        }

    }

    private static class DepthAwareBeanSerializerModifier extends BeanSerializerModifier {

        @Override
        public JsonSerializer<?> modifySerializer(
                SerializationConfig config, BeanDescription desc, JsonSerializer<?> serializer) {
            if (shouldIgnoreClass(desc.getType().getRawClass())) {
                return new IgnoredSerializer();
            }
            return new TypeAwareSerializerWrapper(serializer.withFilterId(DepthAwarePropertyFilter.FILTER_ID));
        }

        @Override
        public JsonSerializer<?> modifyArraySerializer(
                SerializationConfig config, ArrayType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            return new TypeAwareSerializerWrapper(serializer.withFilterId(DepthAwarePropertyFilter.FILTER_ID));
        }

        @Override
        public JsonSerializer<?> modifyCollectionSerializer(
                SerializationConfig config, CollectionType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            return new TypeAwareSerializerWrapper(serializer.withFilterId(DepthAwarePropertyFilter.FILTER_ID));
        }

        @Override
        public JsonSerializer<?> modifyCollectionLikeSerializer(
                SerializationConfig config, CollectionLikeType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            return new TypeAwareSerializerWrapper(serializer.withFilterId(DepthAwarePropertyFilter.FILTER_ID));
        }

        @Override
        public JsonSerializer<?> modifyMapSerializer(
                SerializationConfig config, MapType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            return new TypeAwareMapSerializerWrapper((MapSerializer) serializer.withFilterId(DepthAwarePropertyFilter.FILTER_ID));
        }

        @Override
        public JsonSerializer<?> modifyMapLikeSerializer(
                SerializationConfig config, MapLikeType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            return new TypeAwareMapSerializerWrapper((MapSerializer) serializer.withFilterId(DepthAwarePropertyFilter.FILTER_ID));
        }

        @Override
        public JsonSerializer<?> modifyEnumSerializer(
                SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            return new TypeAwareSerializerWrapper(serializer.withFilterId(DepthAwarePropertyFilter.FILTER_ID));
        }

        @Override
        public JsonSerializer<?> modifyKeySerializer(
                SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            return serializer.withFilterId(DepthAwarePropertyFilter.FILTER_ID);
        }

    }

    private static String getTypeName(Object obj) {
        if (obj == null) {
            return "null";
        }
        return getTypeName(obj.getClass());
    }

    private static String getTypeName(Class type) {
        return type.isArray() ? (type.getComponentType().getName() + "[]") : type.getName();
    }

    private static boolean isPrimitiveType(Class type) {
        return  boolean.class.equals(type) || Boolean.class.equals(type) ||
                byte.class.equals(type) || Byte.class.equals(type) ||
                short.class.equals(type) || Short.class.equals(type) ||
                int.class.equals(type) || Integer.class.equals(type) ||
                float.class.equals(type) || Float.class.equals(type) ||
                long.class.equals(type) || Long.class.equals(type) ||
                double.class.equals(type) || Double.class.equals(type);

    }

    private static boolean isPrimitiveArrayType(Class type) {
        if (type.isArray()) {
            return isPrimitiveType(type.getComponentType());
        }
        return false;
    }

    private static boolean shouldIgnoreClass(Class clazz) {
        for (Class ignoredType : IGNORED_TYPES) {
            if (ignoredType.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    private static int getCurrentSerializedDataSize() {
        JsonWriter jw = threadLocalJsonWriter.get();
        if (jw != null) {
            return jw.size();
        } else {
            return 0;
        }
    }

    private static void writeEmptyArray(JsonGenerator gen) throws IOException {
        gen.writeStartArray();
        gen.writeEndArray();
    }

    private static void wrapAsErroneous(JsonGenerator gen, String fieldName, String errorMessage) throws IOException {
        boolean serializingValue = VALUE_PROPERTY.equals(gen.getOutputContext().getCurrentName());
        if (!serializingValue) {
            gen.writeFieldName(fieldName);
        }
        gen.writeStartObject();
        gen.writeFieldName(TYPE_PROPERTY);
        gen.writeString(String.class.getName());
        gen.writeFieldName(VALUE_PROPERTY);
        gen.writeString("[ERROR] " + errorMessage);
        gen.writeFieldName(ERRONEOUS_PROPERTY);
        gen.writeBoolean(true);
        gen.writeEndObject();
    }

    private static void wrapAsIgnored(JsonGenerator gen, String fieldName, String ignoreReason) throws IOException {
        gen.writeFieldName(fieldName);
        gen.writeStartObject();
        gen.writeFieldName(TYPE_PROPERTY);
        gen.writeString(String.class.getName());
        gen.writeFieldName(VALUE_PROPERTY);
        gen.writeString("[IGNORED] " + ignoreReason);
        gen.writeFieldName(IGNORED_PROPERTY);
        gen.writeBoolean(true);
        gen.writeEndObject();
    }

    private static void wrapAsRedacted(JsonGenerator gen) throws IOException {
        gen.writeFieldName(DATA_REDACTED_PROPERTY);
        gen.writeBoolean(true);
    }

    private static boolean shouldRedactVariable(JsonGenerator gen) {
        String fieldName = gen.getOutputContext().getCurrentName();
        if (fieldName == null || TYPE_PROPERTY.equals(fieldName) || VALUE_PROPERTY.equals(fieldName)) {
            fieldName = gen.getOutputContext().getParent().getCurrentName();
        }
        SerializationContext serializationContext = SerializationContext.get();
        if (serializationContext != null) {
            return DataRedactionHelper.shouldRedactVariable(serializationContext.getDataRedactionContext(), fieldName);
        }
        return DataRedactionHelper.shouldRedactVariable(null, fieldName);
    }

    private static String serializeData(Object obj, DataRedactionContext dataRedactionContext) throws IOException {
        SerializationContext serializationContext = SerializationContext.start();
        serializationContext.dataRedactionContext = dataRedactionContext;
        try {
            ObjectMapper objectMapper = threadLocalObjectMapper.get();
            JsonFactory jsonFactory = objectMapper.getFactory();
            JsonWriter jw = new JsonWriter(jsonFactory._getBufferRecycler());
            threadLocalJsonWriter.set(jw);
            try {
                objectMapper.writeValue(jw, obj);
                return jw.getAndClear();
            } finally {
                threadLocalJsonWriter.remove();
            }
        } finally {
            serializationContext.close();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String serializeValue(Object value, DataRedactionContext dataRedactionContext) throws IOException {
        SerializationContext.destroy();
        try {
            return serializeData(value, dataRedactionContext);
        } finally {
            SerializationContext.destroy();
        }
    }

    public static String wrapAsErroneous(String errorMessage) {
        JSONObject wrappedValue = new JSONObject();
        wrappedValue.put(TYPE_PROPERTY, String.class.getName());
        wrappedValue.put(VALUE_PROPERTY, "[ERROR] " + errorMessage);
        wrappedValue.put(ERRONEOUS_PROPERTY, true);
        return wrappedValue.toString();
    }

    public static String wrapAsIgnored(String ignoreReason) throws IOException {
        JSONObject wrappedValue = new JSONObject();
        wrappedValue.put(TYPE_PROPERTY, String.class.getName());
        wrappedValue.put(VALUE_PROPERTY, "[IGNORED] " + ignoreReason);
        wrappedValue.put(IGNORED_PROPERTY, true);
        return wrappedValue.toString();
    }

    public static String wrapAsRedacted() {
        JSONObject wrappedValue = new JSONObject();
        wrappedValue.put(VALUE_PROPERTY, "null");
        wrappedValue.put(DATA_REDACTED_PROPERTY, true);
        return wrappedValue.toString();
    }

    public static boolean shouldSkipValueFromSerialization(Object value) {
        if (value instanceof InputStream || value instanceof OutputStream) {
            return true;
        }
        // TODO Prevent other known types from serialization
        // TODO Prevent Sidekick classes ("com.runsidekick.agent....") from serialization
        return false;
    }

    public static String generateSerializationErrorMessage(Throwable error) {
        String message = error.getMessage();
        if (message != null && message.length() > MAX_SERIALIZATION_ERROR_MESSAGE_LENGTH) {
            message = message.substring(0, MAX_SERIALIZATION_ERROR_MESSAGE_LENGTH - 4) + " ...";
        }
        return message;
    }

    public static String serializeValueFormatted(Object value) throws IOException {
        SerializationContext.destroy();
        try {
            return threadLocalObjectMapper.get().writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } finally {
            SerializationContext.destroy();
        }
    }

}
