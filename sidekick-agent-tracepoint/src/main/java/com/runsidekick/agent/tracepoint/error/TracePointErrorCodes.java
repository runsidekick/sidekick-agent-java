package com.runsidekick.agent.tracepoint.error;

import com.runsidekick.agent.broker.error.CodedError;
import com.runsidekick.agent.broker.error.impl.SimpleCodedError;

/**
 * @author serkan
 */
public interface TracePointErrorCodes {

    CodedError TRACEPOINT_ALREADY_EXIST =
            new SimpleCodedError(
                    2000,
                    "Tracepoint has been already added in class %s on line %d from client %s");
    CodedError NO_TRACEPOINT_EXIST =
            new SimpleCodedError(
                    2001,
                    "No tracepoint could be found in class %s on line %d from client %s");
    CodedError CLASS_NAME_OR_FILE_NAME_IS_MANDATORY =
            new SimpleCodedError(
                    2002,
                    "Class name or file name is mandatory");
    CodedError LINE_NUMBER_IS_MANDATORY =
            new SimpleCodedError(
                    2003,
                    "Line number is mandatory");
    CodedError NO_TRACEPOINT_EXIST_WITH_ID =
            new SimpleCodedError(
                    2004,
                    "No tracepoint could be found with id %s from client %s");
    CodedError CLIENT_HAS_NO_ACCESS_TO_TRACEPOINT =
            new SimpleCodedError(
                    2005,
                    "Client %s has no access to tracepoint with id %s");

    CodedError PUT_TRACEPOINT_FAILED =
            new SimpleCodedError(
                    2050,
                    "Error occurred while putting tracepoint to class %s on line %d from client %s: %s");
    CodedError SOURCE_CODE_MISMATCH_DETECTED =
            new SimpleCodedError(
                    2051,
                    "Source code mismatch detected while putting tracepoint to class %s on line %d from client %s");

    CodedError UPDATE_TRACEPOINT_FAILED =
            new SimpleCodedError(
                    2100,
                    "Error occurred while updating tracepoint at class %s on line %d from client %s: %s");
    CodedError UPDATE_TRACEPOINT_WITH_ID_FAILED =
            new SimpleCodedError(
                    2101,
                    "Error occurred while updating tracepoint with id %d from client %s: %s");

    CodedError REMOVE_TRACEPOINT_FAILED =
            new SimpleCodedError(
                    2150,
                    "Error occurred while removing tracepoint from class %s on line %d from client %s: %s");
    CodedError REMOVE_TRACEPOINT_WITH_ID_FAILED =
            new SimpleCodedError(
                    2151,
                    "Error occurred while removing tracepoint with id %d from client %s: %s");

    CodedError ENABLE_TRACEPOINT_FAILED =
            new SimpleCodedError(
                    2200,
                    "Error occurred while enabling tracepoint at class %s on line %d from client %s: %s");
    CodedError ENABLE_TRACEPOINT_WITH_ID_FAILED =
            new SimpleCodedError(
                    2201,
                    "Error occurred while enabling tracepoint with id %d from client %s: %s");

    CodedError DISABLE_TRACEPOINT_FAILED =
            new SimpleCodedError(
                    2250,
                    "Error occurred while disabling tracepoint at class %s on line %d from client %s: %s");
    CodedError DISABLE_TRACEPOINT_WITH_ID_FAILED =
            new SimpleCodedError(
                    2251,
                    "Error occurred while disabling tracepoint with id %d from client %s: %s");

}
