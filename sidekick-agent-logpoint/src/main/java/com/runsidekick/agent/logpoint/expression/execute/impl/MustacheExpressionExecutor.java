package com.runsidekick.agent.logpoint.expression.execute.impl;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.runsidekick.agent.api.dataredaction.DataRedactionContext;
import com.runsidekick.agent.core.util.map.ConcurrentWeakMap;
import com.runsidekick.agent.dataredaction.DataRedactionHelper;
import com.runsidekick.agent.logpoint.expression.execute.LogPointExpressionExecutor;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author yasin
 */
public class MustacheExpressionExecutor implements LogPointExpressionExecutor {

    private static final ReflectionObjectHandler objectHandler = createObjectHandler();
    private static final ConcurrentWeakMap<String, Mustache> expressionMap = new ConcurrentWeakMap();
    private static final DefaultMustacheFactory mf = new DefaultMustacheFactory();
    private static final ThreadLocal<StringWriter> threadLocalWriter = ThreadLocal.withInitial(StringWriter::new);
    private static final ThreadLocal<DataRedactionContext> threadLocalDataRedactionContext =
            new ThreadLocal<>();

    public MustacheExpressionExecutor() {
        mf.setObjectHandler(objectHandler);
    }

    @Override
    public String execute(DataRedactionContext dataRedactionContext, String expression, Map<String, Object> variables) {
        threadLocalDataRedactionContext.set(dataRedactionContext);
        StringWriter writer = threadLocalWriter.get();
        writer.getBuffer().setLength(0);
        Mustache mustache = expressionMap.get(expression);
        if (mustache == null) {
            mustache = mf.compile(new StringReader(expression), UUID.randomUUID().toString());
            Mustache existingMustache = expressionMap.putIfAbsent(expression, mustache);
            if (existingMustache != null) {
                mustache = existingMustache;
            }
        }
        mustache.execute(writer, variables);
        writer.flush();
        return writer.getBuffer().toString();
    }

    private static ReflectionObjectHandler createObjectHandler() {
        return new ReflectionObjectHandler() {
            @Override
            public Object coerce(final Object object) {
                if (object != null) {
                    if (object instanceof List) {
                        return new ListMap(object);
                    }
                    if (object.getClass().isArray()) {
                        return new ArrayMap(object);
                    }
                }
                return super.coerce(object);
            }

            @Override
            protected AccessibleObject findMember(Class sClass, String name) {
                if (DataRedactionHelper.shouldRedactVariable(threadLocalDataRedactionContext.get(), name)) {
                    return null;
                }
                return super.findMember(sClass, name);
            }


        };
    }

    private static class ListMap extends AbstractMap<Object, Object> implements Iterable<Object> {
        private final Object object;

        public ListMap(Object object) {
            this.object = object;
        }

        @Override
        public Object get(Object key) {
            try {
                int index = Integer.parseInt(key.toString());
                return ((List)object).get(index);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                return null;
            }
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns an iterator over a set of elements of type T.
         *
         * @return an Iterator.
         */
        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>() {

                int index = 0;
                int length = ((List)object).size();

                @Override
                public boolean hasNext() {
                    return index < length;
                }

                @Override
                public Object next() {
                    return ((List)object).get(index++);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    private static class ArrayMap extends AbstractMap<Object, Object> {
        private final Object object;

        public ArrayMap(Object object) {
            this.object = object;
        }

        @Override
        public Object get(Object key) {
            try {
                int index = Integer.parseInt(key.toString());
                return Array.get(object, index);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                return null;
            }
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }

    }
}
