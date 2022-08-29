package com.runsidekick.agent.probe.error;

import com.runsidekick.agent.broker.error.CodedError;
import com.runsidekick.agent.broker.error.impl.SimpleCodedError;

/**
 * @author serkan
 */
public interface ProbeErrorCodes {

    CodedError INSTRUMENTATION_IS_NOT_ACTIVE =
            new SimpleCodedError(
                    1000,
                    "Couldn't activate instrumentation support. So probes are not supported");
    CodedError BOOTSTRAP_CLASSLOADER_NOT_SUPPORTED =
            new SimpleCodedError(
                    1001,
                    "Probe operation has failed for class %s " +
                            "as probes are not supported on classes loaded by bootstrap classloader");
    CodedError UNABLE_TO_FIND_CLASS =
            new SimpleCodedError(
                    1002,
                    "Unable to find class %s");
    CodedError NO_METHOD_COULD_BE_FOUND =
            new SimpleCodedError(
                    1003,
                    "No method could be found in class %s on line %d from client %s");
    CodedError LINE_NO_IS_NOT_AVAILABLE =
            new SimpleCodedError(
                    1004,
                    "Line %d is not available in class %s for probe");
    CodedError NO_PROBE_EXIST =
            new SimpleCodedError(
                    1005,
                    "No probe could be found with id %s");

    CodedError CONDITION_CHECK_FAILED =
            new SimpleCodedError(
                    1900,
                    "Error occurred while checking condition '%s': %s");
    CodedError CONDITION_EXPRESSION_SYNTAX_CHECK_FAILED =
            new SimpleCodedError(
                    1901,
                    "Syntax check failed while checking condition '%s': %s");
    CodedError UNABLE_TO_FIND_METADATA_OF_VARIABLE_FOR_CONDITION =
            new SimpleCodedError(
                    1902,
                    "Unable to find metadata of variable %s while checking condition");
    CodedError UNABLE_TO_FIND_TYPE_OF_VARIABLE_FOR_CONDITION =
            new SimpleCodedError(
                    1903,
                    "Unable to find type of variable %s while checking condition");
    CodedError UNABLE_TO_FIND_PROPERTY_FOR_CONDITION =
            new SimpleCodedError(
                    1904,
                    "Unable to find property over class %s while evaluating condition: %s");
    CodedError UNABLE_TO_GET_PROPERTY_FOR_CONDITION =
            new SimpleCodedError(
                    1905,
                    "Unable to get property over class %s while evaluating condition: %s");
    CodedError UNKNOWN_PLACEHOLDER_FOR_CONDITION =
            new SimpleCodedError(
                    1906,
                    "Unknown placeholder detected while checking condition: %s");

}
