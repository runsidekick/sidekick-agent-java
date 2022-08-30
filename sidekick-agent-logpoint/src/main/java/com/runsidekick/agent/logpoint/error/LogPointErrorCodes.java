package com.runsidekick.agent.logpoint.error;

import com.runsidekick.agent.broker.error.CodedError;
import com.runsidekick.agent.broker.error.impl.SimpleCodedError;

/**
 * @author yasin
 */
public interface LogPointErrorCodes {

    CodedError LOGPOINT_ALREADY_EXIST =
            new SimpleCodedError(
                    3000,
                    "Logpoint has been already added in class %s on line %d from client %s");
    CodedError NO_LOGPOINT_EXIST =
            new SimpleCodedError(
                    3001,
                    "No logpoint could be found in class %s on line %d from client %s");
    CodedError CLASS_NAME_OR_FILE_NAME_IS_MANDATORY =
            new SimpleCodedError(
                    3002,
                    "Class name or file name is mandatory");
    CodedError LINE_NUMBER_IS_MANDATORY =
            new SimpleCodedError(
                    3003,
                    "Line number is mandatory");
    CodedError NO_LOGPOINT_EXIST_WITH_ID =
            new SimpleCodedError(
                    3004,
                    "No logpoint could be found with id %s from client %s");
    CodedError CLIENT_HAS_NO_ACCESS_TO_LOGPOINT =
            new SimpleCodedError(
                    3005,
                    "Client %s has no access to logpoint with id %s");

    CodedError PUT_LOGPOINT_FAILED =
            new SimpleCodedError(
                    3050,
                    "Error occurred while putting logpoint to class %s on line %d from client %s: %s");
    CodedError SOURCE_CODE_MISMATCH_DETECTED =
            new SimpleCodedError(
                    3051,
                    "Source code mismatch detected while putting logpoint to class %s on line %d from client %s");

    CodedError UPDATE_LOGPOINT_FAILED =
            new SimpleCodedError(
                    3100,
                    "Error occurred while updating logpoint at class %s on line %d from client %s: %s");
    CodedError UPDATE_LOGPOINT_WITH_ID_FAILED =
            new SimpleCodedError(
                    3101,
                    "Error occurred while updating logpoint with id %d from client %s: %s");

    CodedError REMOVE_LOGPOINT_FAILED =
            new SimpleCodedError(
                    3150,
                    "Error occurred while removing logpoint from class %s on line %d from client %s: %s");
    CodedError REMOVE_LOGPOINT_WITH_ID_FAILED =
            new SimpleCodedError(
                    3151,
                    "Error occurred while removing logpoint with id %d from client %s: %s");

    CodedError ENABLE_LOGPOINT_FAILED =
            new SimpleCodedError(
                    3200,
                    "Error occurred while enabling logpoint at class %s on line %d from client %s: %s");
    CodedError ENABLE_LOGPOINT_WITH_ID_FAILED =
            new SimpleCodedError(
                    3201,
                    "Error occurred while enabling logpoint with id %d from client %s: %s");

    CodedError DISABLE_LOGPOINT_FAILED =
            new SimpleCodedError(
                    3250,
                    "Error occurred while disabling logpoint at class %s on line %d from client %s: %s");
    CodedError DISABLE_LOGPOINT_WITH_ID_FAILED =
            new SimpleCodedError(
                    3251,
                    "Error occurred while disabling logpoint with id %d from client %s: %s");

}
