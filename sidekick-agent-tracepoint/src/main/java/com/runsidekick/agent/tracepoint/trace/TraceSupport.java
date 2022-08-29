package com.runsidekick.agent.tracepoint.trace;

import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.map.ConcurrentWeakMap;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * @author serkan
 */
public final class TraceSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceSupport.class);

    private static final ConcurrentWeakMap<ClassLoader, ThundraMetadata> thundraMetadataCache =
            new ConcurrentWeakMap();
    private static final ConcurrentWeakMap<ClassLoader, OpenTracingMetadata> openTracingMetadataCache =
            new ConcurrentWeakMap();

    private static boolean thundraCheckDisabled = false;
    private static boolean openTracingCheckDisabled = false;

    private TraceSupport() {
    }

    public static TraceContext getTraceContext(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        TraceContext traceContext = getTraceContextFromThundra(classLoader);
        if (traceContext == null) {
            traceContext = getTraceContextFromOpenTracing(classLoader);
        }
        return traceContext;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static class ThundraMetadata {

        private static final ThundraMetadata EMPTY = new ThundraMetadata();

        private final Class<?> traceSupportClass;
        private final Method getActiveSpanMethod;
        private final Method getTraceIdMethod;
        private final Method getTransactionIdMethod;
        private final Method getSpanIdMethod;

        private ThundraMetadata() {
            this.traceSupportClass = null;
            this.getActiveSpanMethod = null;
            this.getTraceIdMethod = null;
            this.getTransactionIdMethod = null;
            this.getSpanIdMethod = null;
        }

        private ThundraMetadata(Class<?> traceSupportClass, Method getActiveSpanMethod,
                                Method getTraceIdMethod, Method getTransactionIdMethod, Method getSpanIdMethod) {
            this.traceSupportClass = traceSupportClass;
            this.getActiveSpanMethod = getActiveSpanMethod;
            this.getTraceIdMethod = getTraceIdMethod;
            this.getTransactionIdMethod = getTransactionIdMethod;
            this.getSpanIdMethod = getSpanIdMethod;
        }

        private boolean isEmpty() {
            return traceSupportClass == null;
        }

    }

    private static ThundraMetadata createThundraMetadata(ClassLoader classLoader) {
        try {
            Class<?> traceSupportClass = classLoader.loadClass("io.thundra.agent.trace.TraceSupport");
            Method getActiveSpanMethod = traceSupportClass.getMethod("getActiveSpan");

            Class<?> spanClass = classLoader.loadClass("io.thundra.agent.trace.span.ThundraSpan");
            Method getTraceIdMethod = spanClass.getMethod("getTraceId");
            Method getTransactionIdMethod = spanClass.getMethod("getTransactionId");
            Method getSpanIdMethod = spanClass.getMethod("getId");

            return new ThundraMetadata(
                    traceSupportClass, getActiveSpanMethod,
                    getTraceIdMethod, getTransactionIdMethod, getSpanIdMethod);
        } catch (ClassNotFoundException | NoSuchMethodException | NoClassDefFoundError | NoSuchMethodError e) {
            LOGGER.debug("Unable to get Thundra metadata", e);
            return ThundraMetadata.EMPTY;
        }
    }

    private static ThundraMetadata getThundraMetadata(ClassLoader classLoader) {
        ThundraMetadata thundraMetadata = thundraMetadataCache.get(classLoader);
        if (thundraMetadata == null) {
            thundraMetadata = createThundraMetadata(classLoader);
            ThundraMetadata existingThundraMetadata =
                    thundraMetadataCache.putIfAbsent(classLoader, thundraMetadata);
            if (existingThundraMetadata != null) {
                thundraMetadata = existingThundraMetadata;
            }
        }
        return thundraMetadata;
    }

    private static TraceContext getTraceContextFromThundra(ClassLoader classLoader) {
        if (thundraCheckDisabled) {
            return null;
        }
        try {
            ThundraMetadata thundraMetadata = getThundraMetadata(classLoader);
            if (thundraMetadata.isEmpty()) {
                thundraCheckDisabled = true;
            } else {
                Object span = thundraMetadata.getActiveSpanMethod.invoke(thundraMetadata.traceSupportClass);
                if (span != null) {
                    String traceId = (String) thundraMetadata.getTraceIdMethod.invoke(span);
                    String transactionId = (String) thundraMetadata.getTransactionIdMethod.invoke(span);
                    String spanId = (String) thundraMetadata.getSpanIdMethod.invoke(span);
                    return new TraceContext(traceId, transactionId, spanId);
                }
            }
            /*
            io.thundra.agent.trace.span.ThundraSpan span = io.thundra.agent.trace.TraceSupport.getActiveSpan();
            if (span != null) {
                return new TraceContext(span.getTraceId(), span.getTransactionId(), span.getId());
            }
            */
        } catch (Throwable t) {
            LOGGER.debug("Unable to get trace context from Thundra", t);
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static class OpenTracingMetadata {

        private static final OpenTracingMetadata EMPTY = new OpenTracingMetadata();

        private final Class<?> globalTracerClass;
        private final Method getMethod;
        private final Method activeSpanMethod;
        private final Method contextMethod;
        private final Method toTraceIdMethod;
        private final Method toSpanIdMethod;

        private OpenTracingMetadata() {
            this.globalTracerClass = null;
            this.getMethod = null;
            this.activeSpanMethod = null;
            this.contextMethod = null;
            this.toTraceIdMethod = null;
            this.toSpanIdMethod = null;
        }

        private OpenTracingMetadata(Class<?> globalTracerClass, Method getMethod,
                                    Method activeSpanMethod, Method contextMethod,
                                    Method toTraceIdMethod, Method toSpanIdMethod) {
            this.globalTracerClass = globalTracerClass;
            this.getMethod = getMethod;
            this.activeSpanMethod = activeSpanMethod;
            this.contextMethod = contextMethod;
            this.toTraceIdMethod = toTraceIdMethod;
            this.toSpanIdMethod = toSpanIdMethod;
        }

        private boolean isEmpty() {
            return globalTracerClass == null;
        }

    }

    private static OpenTracingMetadata createOpenTracingMetadata(ClassLoader classLoader) {
        try {
            Class<?> globalTracerClass = classLoader.loadClass("io.opentracing.util.GlobalTracer");
            Method getMethod = globalTracerClass.getMethod("get");

            Class<?> tracerClass = classLoader.loadClass("io.opentracing.Tracer");
            Method activeSpanMethod = tracerClass.getMethod("activeSpan");

            Class<?> spanClass = classLoader.loadClass("io.opentracing.Span");
            Method contextMethod = spanClass.getMethod("context");

            Class<?> spanContextClass = classLoader.loadClass("io.opentracing.SpanContext");
            Method toTraceIdMethod = spanContextClass.getMethod("toTraceId");
            Method toSpanIdMethod = spanContextClass.getMethod("toSpanId");

            return new OpenTracingMetadata(
                    globalTracerClass, getMethod,
                    activeSpanMethod, contextMethod,
                    toTraceIdMethod, toSpanIdMethod);
        } catch (ClassNotFoundException | NoSuchMethodException | NoClassDefFoundError | NoSuchMethodError e) {
            LOGGER.debug("Unable to get OpenTracing metadata", e);
            return OpenTracingMetadata.EMPTY;
        }
    }

    private static OpenTracingMetadata getOpenTracingMetadata(ClassLoader classLoader) {
        OpenTracingMetadata openTracingMetadata = openTracingMetadataCache.get(classLoader);
        if (openTracingMetadata == null) {
            openTracingMetadata = createOpenTracingMetadata(classLoader);
            OpenTracingMetadata existingOpenTracingMetadata =
                    openTracingMetadataCache.putIfAbsent(classLoader, openTracingMetadata);
            if (existingOpenTracingMetadata != null) {
                openTracingMetadata = existingOpenTracingMetadata;
            }
        }
        return openTracingMetadata;
    }

    private static TraceContext getTraceContextFromOpenTracing(ClassLoader classLoader) {
        if (openTracingCheckDisabled) {
            return null;
        }
        try {
            OpenTracingMetadata openTracingMetadata = getOpenTracingMetadata(classLoader);
            if (openTracingMetadata.isEmpty()) {
                openTracingCheckDisabled = true;
            } else {
                Object tracer = openTracingMetadata.getMethod.invoke(openTracingMetadata.globalTracerClass);
                if (tracer != null) {
                    Object span = openTracingMetadata.activeSpanMethod.invoke(tracer);
                    if (span != null) {
                        Object spanContext = openTracingMetadata.contextMethod.invoke(span);
                        if (spanContext != null) {
                            String traceId = (String) openTracingMetadata.toTraceIdMethod.invoke(spanContext);
                            String spanId = (String) openTracingMetadata.toSpanIdMethod.invoke(spanContext);
                            return new TraceContext(traceId, null, spanId);
                        }
                    }
                }
            }
            /*
            io.opentracing.Tracer tracer = io.opentracing.util.GlobalTracer.get();
            if (tracer != null) {
                io.opentracing.Span span = tracer.activeSpan();
                if (span != null) {
                    io.opentracing.SpanContext spanContext = span.context();
                    if (spanContext != null) {
                        return new TraceContext(spanContext.toTraceId(), null, spanContext.toSpanId());
                    }
                }
            }
            */
        } catch (Throwable t) {
            LOGGER.debug("Unable to get trace context from OpenTracing", t);
        }
        return null;
    }

}
