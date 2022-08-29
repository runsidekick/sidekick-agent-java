package com.runsidekick.agent.core.test;

import com.runsidekick.agent.core.util.ExceptionUtils;
import com.runsidekick.agent.core.initialize.EnvironmentInitializerManager;
import org.junit.After;
import org.junit.Before;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.fail;

/**
 * @author serkan
 */
public abstract class BaseSidekickTest {

    public static final int ASSERT_TRUE_EVENTUALLY_TIMEOUT = 30;

    static {
        EnvironmentInitializerManager.ensureInitialized();
    }

    @Before
    public void setUp() throws Exception {
        try {
            beforeSetUp();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        EnvironmentTestUtils.saveSystemProperties();

        try {
            afterSetUp();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void beforeSetUp() throws Exception {
    }

    protected void afterSetUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        try {
            beforeTearDown();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        EnvironmentTestUtils.restoreSystemProperties();
        EnvironmentTestUtils.resetEnvironmentVariables();

        try {
            afterTearDown();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void beforeTearDown() throws Exception {
    }

    protected void afterTearDown() throws Exception {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void sleepMillis(int millis) {
        try {
            MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void sleepSeconds(int seconds) {
        try {
            SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void sleepAtLeastMillis(long sleepFor) {
        boolean interrupted = false;
        try {
            long remainingNanos = MILLISECONDS.toNanos(sleepFor);
            long sleepUntil = System.nanoTime() + remainingNanos;
            while (remainingNanos > 0) {
                try {
                    NANOSECONDS.sleep(remainingNanos);
                } catch (InterruptedException e) {
                    interrupted = true;
                } finally {
                    remainingNanos = sleepUntil - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void sleepAtLeastSeconds(long seconds) {
        sleepAtLeastMillis(seconds * 1000);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @FunctionalInterface
    public interface AssertTask {

        void run() throws Exception;
    }

    public static void assertFalseEventually(AssertTask task, long timeoutSeconds) {
        AssertionError error = null;
        // we are going to check five times a second
        int sleepMillis = 200;
        long iterations = timeoutSeconds * 5;
        long deadline = System.currentTimeMillis() + SECONDS.toMillis(timeoutSeconds);
        for (int i = 0; i < iterations && System.currentTimeMillis() < deadline; i++) {
            try {
                try {
                    task.run();
                } catch (Exception e) {
                    ExceptionUtils.sneakyThrow(e);
                }
            } catch (AssertionError e) {
                return;
            }
            sleepMillis(sleepMillis);
        }
        fail("assertFalseEventually() failed without AssertionError!");
    }

    public static void assertFalseEventually(AssertTask task) {
        assertFalseEventually(task, ASSERT_TRUE_EVENTUALLY_TIMEOUT);
    }

    public static void assertTrueEventually(String message, AssertTask task, long timeoutSeconds) {
        AssertionError error = null;
        // we are going to check five times a second
        int sleepMillis = 200;
        long iterations = timeoutSeconds * 5;
        long deadline = System.currentTimeMillis() + SECONDS.toMillis(timeoutSeconds);
        for (int i = 0; i < iterations && System.currentTimeMillis() < deadline; i++) {
            try {
                try {
                    task.run();
                } catch (Exception e) {
                    ExceptionUtils.sneakyThrow(e);
                }
                return;
            } catch (AssertionError e) {
                error = e;
            }
            sleepMillis(sleepMillis);
        }
        if (error != null) {
            throw error;
        }
        fail("assertTrueEventually() failed without AssertionError! " + message);
    }

    public static void assertTrueEventually(AssertTask task, long timeoutSeconds) {
        assertTrueEventually(null, task, timeoutSeconds);
    }

    public static void assertTrueEventually(String message, AssertTask task) {
        assertTrueEventually(message, task, ASSERT_TRUE_EVENTUALLY_TIMEOUT);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
