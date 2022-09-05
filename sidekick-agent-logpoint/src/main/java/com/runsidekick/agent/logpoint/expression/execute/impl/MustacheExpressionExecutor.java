package com.runsidekick.agent.logpoint.expression.execute.impl;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.runsidekick.agent.core.util.map.ConcurrentWeakMap;
import com.runsidekick.agent.logpoint.expression.execute.LogPointExpressionExecutor;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * @author yasin
 */
public class MustacheExpressionExecutor implements LogPointExpressionExecutor {

    private static final ReflectionObjectHandler objectHandler = createObjectHandler();
    private static final ConcurrentWeakMap<String, Mustache> expressionMap = new ConcurrentWeakMap();
    private static final DefaultMustacheFactory mf = new DefaultMustacheFactory();
    private static final ThreadLocal<StringWriter> threadLocalWriter = ThreadLocal.withInitial(StringWriter::new);

    public MustacheExpressionExecutor() {
        mf.setObjectHandler(objectHandler);
    }

    @Override
    public String execute(String expression, Map<String, String> variables) {
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
                if (object != null && object instanceof List) {
                    return new ListMap(object);
                }
                return super.coerce(object);
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
}
