package com.runsidekick.agent.core.initialize;

import com.runsidekick.agent.core.instance.InstanceProvider;
import com.runsidekick.agent.core.instance.InstanceScope;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author serkan
 */
public class EnvironmentInitializerTest {

    @Test
    public void environmentInitializersShouldBeAbleToDiscoveredAndInitialized() {
        InstanceProvider.clearScope(InstanceScope.GLOBAL);
        EnvironmentInitializerManager.reset();

        Collection<EnvironmentInitializer> environmentInitializers =
                EnvironmentInitializerManager.getEnvironmentInitializers();
        assertEquals(1, environmentInitializers.size());

        EnvironmentInitializer environmentInitializer =
                environmentInitializers.iterator().next();
        assertTrue(environmentInitializer instanceof TestEnvironmentInitializer);

        TestEnvironmentInitializer testEnvironmentInitializer =
                (TestEnvironmentInitializer) environmentInitializer;
        assertEquals(0, testEnvironmentInitializer.getInitializeCounter());

        EnvironmentInitializerManager.ensureInitialized();
        assertEquals(1, testEnvironmentInitializer.getInitializeCounter());

        EnvironmentInitializerManager.ensureInitialized();
        assertEquals(1, testEnvironmentInitializer.getInitializeCounter());
    }

}
