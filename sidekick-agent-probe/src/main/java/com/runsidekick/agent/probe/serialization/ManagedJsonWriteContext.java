package com.runsidekick.agent.probe.serialization;

import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonWriteContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author serkan
 */
class ManagedJsonWriteContext extends JsonWriteContext {

    private final Map<String, Integer> fieldNameOccurrences = new HashMap<>(16);

    ManagedJsonWriteContext(JsonWriteContext jsonWriteContext) {
        super(getType(jsonWriteContext), jsonWriteContext.getParent(), jsonWriteContext.getDupDetector());
    }

    ManagedJsonWriteContext(int type, JsonWriteContext parent, DupDetector dups) {
        super(type, parent, dups);
    }

    private static int getType(JsonWriteContext jsonWriteContext) {
        if (jsonWriteContext.inRoot()) {
            return JsonStreamContext.TYPE_ROOT;
        } else if (jsonWriteContext.inArray()) {
            return JsonStreamContext.TYPE_ARRAY;
        } else if (jsonWriteContext.inObject()) {
            return JsonStreamContext.TYPE_OBJECT;
        } else {
            throw new IllegalArgumentException("Unknown type: " + jsonWriteContext.getTypeDesc());
        }
    }

    @Override
    public JsonWriteContext createChildArrayContext() {
        ManagedJsonWriteContext ctxt = (ManagedJsonWriteContext) _child;
        if (ctxt == null) {
            _child = ctxt = new ManagedJsonWriteContext(TYPE_ARRAY, this, (_dups == null) ? null : _dups.child());
            return ctxt;
        }
        return ctxt.reset(TYPE_ARRAY);
    }

    @Override
    public JsonWriteContext createChildObjectContext() {
        ManagedJsonWriteContext ctxt = (ManagedJsonWriteContext) _child;
        if (ctxt == null) {
            _child = ctxt = new ManagedJsonWriteContext(TYPE_OBJECT, this, (_dups == null) ? null : _dups.child());
            return ctxt;
        }
        return ctxt.reset(TYPE_OBJECT);
    }

    @Override
    public JsonWriteContext reset(int type) {
        fieldNameOccurrences.clear();
        return super.reset(type);
    }

    String checkAndGetFieldName(String fieldName) {
        int occurrence = fieldNameOccurrences.getOrDefault(fieldName, 0);
        int newOccurrence = occurrence + 1;
        fieldNameOccurrences.put(fieldName, newOccurrence);
        if (newOccurrence == 1) {
            return fieldName;
        } else {
            return fieldName + " (" + newOccurrence + ")";
        }
    }

}
