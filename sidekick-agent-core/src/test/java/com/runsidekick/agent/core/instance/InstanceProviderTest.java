package com.runsidekick.agent.core.instance;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author serkan
 */
public class InstanceProviderTest {

    private final TestInstanceCreator testInstanceCreator = new TestInstanceCreator();

    @Test
    public void globalScopedInstanceShouldBeProvidedSuccessfully() {
        TestInstance testInstance1 =
                InstanceProvider.getInstance(
                        TestInstance.class,
                        InstanceScope.GLOBAL,
                        testInstanceCreator);
        TestInstance testInstance2 =
                InstanceProvider.getInstance(
                        TestInstance.class,
                        InstanceScope.GLOBAL,
                        testInstanceCreator);
        assertEquals(testInstance1, testInstance2);
    }

    @Test
    public void threadLocalScopedInstanceShouldBeProvidedSuccessfully() throws InterruptedException {
        TestInstance testInstance1 =
                InstanceProvider.getInstance(
                        TestInstance.class,
                        InstanceScope.THREAD_LOCAL,
                        testInstanceCreator);
        TestInstance testInstance2 =
                InstanceProvider.getInstance(
                        TestInstance.class,
                        InstanceScope.THREAD_LOCAL,
                        testInstanceCreator);
        AtomicReference<TestInstance> testInstanceRef1a = new AtomicReference<TestInstance>();
        AtomicReference<TestInstance> testInstanceRef1b = new AtomicReference<TestInstance>();
        AtomicReference<TestInstance> testInstanceRef2a = new AtomicReference<TestInstance>();
        AtomicReference<TestInstance> testInstanceRef2b = new AtomicReference<TestInstance>();

        Thread t1 = new Thread() {
            @Override
            public void run() {
                TestInstance testInstance1a =
                        InstanceProvider.getInstance(
                                TestInstance.class,
                                InstanceScope.THREAD_LOCAL,
                                testInstanceCreator);
                TestInstance testInstance1b =
                        InstanceProvider.getInstance(
                                TestInstance.class,
                                InstanceScope.THREAD_LOCAL,
                                testInstanceCreator);
                testInstanceRef1a.set(testInstance1a);
                testInstanceRef1b.set(testInstance1b);
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                TestInstance testInstance2a =
                        InstanceProvider.getInstance(
                                TestInstance.class,
                                InstanceScope.THREAD_LOCAL,
                                testInstanceCreator);
                TestInstance testInstance2b =
                        InstanceProvider.getInstance(
                                TestInstance.class,
                                InstanceScope.THREAD_LOCAL,
                                testInstanceCreator);
                testInstanceRef2a.set(testInstance2a);
                testInstanceRef2b.set(testInstance2b);
            }
        };

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertEquals(testInstance1, testInstance2);
        assertEquals(testInstanceRef1a.get(), testInstanceRef1b.get());
        assertEquals(testInstanceRef2a.get(), testInstanceRef2b.get());

        assertNotEquals(testInstance1, testInstanceRef1a.get());
        assertNotEquals(testInstanceRef1a.get(), testInstanceRef2a.get());
        assertNotEquals(testInstance2, testInstanceRef1b.get());
        assertNotEquals(testInstanceRef1b.get(), testInstanceRef2b.get());
    }

    @Test
    public void inheritableThreadLocalScopedInstanceShouldBeProvidedSuccessfully() throws InterruptedException {
        TestInstance testInstance1 =
                InstanceProvider.getInstance(
                        TestInstance.class,
                        InstanceScope.INHERITABLE_THREAD_LOCAL,
                        testInstanceCreator);
        TestInstance testInstance2 =
                InstanceProvider.getInstance(
                        TestInstance.class,
                        InstanceScope.INHERITABLE_THREAD_LOCAL,
                        testInstanceCreator);
        AtomicReference<TestInstance> testInstanceRef1a = new AtomicReference<TestInstance>();
        AtomicReference<TestInstance> testInstanceRef1b = new AtomicReference<TestInstance>();
        AtomicReference<TestInstance> testInstanceRef2a = new AtomicReference<TestInstance>();
        AtomicReference<TestInstance> testInstanceRef2b = new AtomicReference<TestInstance>();

        Thread t1 = new Thread() {
            @Override
            public void run() {
                TestInstance testInstance1a =
                        InstanceProvider.getInstance(
                                TestInstance.class,
                                InstanceScope.INHERITABLE_THREAD_LOCAL,
                                testInstanceCreator);
                TestInstance testInstance1b =
                        InstanceProvider.getInstance(
                                TestInstance.class,
                                InstanceScope.INHERITABLE_THREAD_LOCAL,
                                testInstanceCreator);
                testInstanceRef1a.set(testInstance1a);
                testInstanceRef1b.set(testInstance1b);
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                TestInstance testInstance2a =
                        InstanceProvider.getInstance(
                                TestInstance.class,
                                InstanceScope.INHERITABLE_THREAD_LOCAL,
                                testInstanceCreator);
                TestInstance testInstance2b =
                        InstanceProvider.getInstance(
                                TestInstance.class,
                                InstanceScope.INHERITABLE_THREAD_LOCAL,
                                testInstanceCreator);
                testInstanceRef2a.set(testInstance2a);
                testInstanceRef2b.set(testInstance2b);
            }
        };

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertEquals(testInstance1, testInstance2);
        assertEquals(testInstanceRef1a.get(), testInstanceRef1b.get());
        assertEquals(testInstanceRef2a.get(), testInstanceRef2b.get());

        assertEquals(testInstance1, testInstanceRef1a.get());
        assertEquals(testInstanceRef1a.get(), testInstanceRef2a.get());
        assertEquals(testInstance2, testInstanceRef1b.get());
        assertEquals(testInstanceRef1b.get(), testInstanceRef2b.get());
    }

    @Test
    public void prototypeScopedInstanceShouldBeProvidedSuccessfully() {
        TestInstance testInstance1 =
                InstanceProvider.getInstance(
                        TestInstance.class,
                        InstanceScope.PROTOTYPE,
                        testInstanceCreator);
        TestInstance testInstance2 =
                InstanceProvider.getInstance(
                        TestInstance.class,
                        InstanceScope.PROTOTYPE,
                        testInstanceCreator);
        assertNotEquals(testInstance1, testInstance2);
    }

    private static class TestInstanceCreator implements InstanceCreator {

        @Override
        public <T> T create(Class<T> clazz) {
            assert clazz == TestInstance.class : "Only " + TestInstance.class.getName() + " is supported";
            return (T) new TestInstance();
        }

    }

    public static class TestInstance {
    }

}
