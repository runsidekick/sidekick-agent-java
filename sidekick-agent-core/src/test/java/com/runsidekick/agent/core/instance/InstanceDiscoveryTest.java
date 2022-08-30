package com.runsidekick.agent.core.instance;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author serkan
 */
public class InstanceDiscoveryTest {

    @Test
    public void instanceShouldBeAbleToDiscovered() {
        TestDiscoveredInstance testDiscoveredInstance =
                InstanceDiscovery.instanceOf(TestDiscoveredInstance.class);
        assertNotNull(testDiscoveredInstance);
        assertTrue(testDiscoveredInstance instanceof TestDiscoveredInstanceImpl);
    }

}
