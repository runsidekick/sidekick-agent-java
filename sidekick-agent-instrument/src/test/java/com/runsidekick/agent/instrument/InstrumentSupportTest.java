package com.runsidekick.agent.instrument;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author serkan
 */
public class InstrumentSupportTest {

    @Test
    public void instrumentationSupportShouldBeAbleToActivated() {
        InstrumentSupport.ensureActivated();
        assertNotNull(InstrumentSupport.getInstrumentation());
    }

}
