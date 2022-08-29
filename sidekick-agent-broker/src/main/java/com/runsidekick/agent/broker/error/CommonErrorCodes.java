package com.runsidekick.agent.broker.error;

import com.runsidekick.agent.broker.error.impl.SimpleCodedError;

/**
 * @author serkan
 */
public interface CommonErrorCodes {

    CodedError UNKNOWN = new SimpleCodedError(0, "Unknown");

}
