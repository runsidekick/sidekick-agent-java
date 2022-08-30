package com.runsidekick.agent.core.initialize;

import com.runsidekick.agent.core.instance.InstanceDiscovery;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.ThreadUtils;
import com.runsidekick.agent.core.terminate.EnvironmentTerminatorManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager class to manage environment
 * {@link EnvironmentInitializer} related operations.
 *
 * @author serkan
 */
public final class EnvironmentInitializerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentInitializerManager.class);

    private static boolean initialized = false;
    private static List<EnvironmentInitializer> environmentInitializers =
            Collections.unmodifiableList(
                    InstanceDiscovery.instancesOf(EnvironmentInitializer.class));
    private static Set<String> ignoredInitializerPrefixes = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private EnvironmentInitializerManager() {
    }

    private static boolean isEnvironmentInitializerIgnored(EnvironmentInitializer environmentInitializer) {
        for (String prefix : ignoredInitializerPrefixes) {
            if (environmentInitializer.getClass().getName().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static void registerIgnoredInitializerPrefix(String prefix) {
        ignoredInitializerPrefixes.add(prefix);
    }

    public static void removeIgnoredInitializerPrefix(String prefix) {
        ignoredInitializerPrefixes.remove(prefix);
    }

    public static synchronized void ensureInitialized() {
        if (!initialized) {
            try {
                preInit();
                init();
            } finally {
                initialized = true;
            }
        }
    }

    public static synchronized CompletableFuture ensureInitializedAsyncIfPossible() {
        if (!initialized) {
            try {
                preInit();
                return initAsyncIfPossible();
            } finally {
                initialized = true;
            }
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    public static synchronized List<EnvironmentAsyncInitializer> ensureInitializedNonAsyncs() {
        if (!initialized) {
            try {
                preInit();
                return initNonAsyncs();
            } finally {
                initialized = true;
            }
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    synchronized static void reset() {
        environmentInitializers =
                Collections.unmodifiableList(
                        InstanceDiscovery.instancesOf(EnvironmentInitializer.class));
        initialized = false;
    }

    public static Collection<EnvironmentInitializer> getEnvironmentInitializers() {
        return environmentInitializers;
    }

    private static void preInit() {
        registerShutdownHook();

        for (EnvironmentInitializer environmentInitializer : environmentInitializers) {
            try {
                if (isEnvironmentInitializerIgnored(environmentInitializer)) {
                    continue;
                }
                long start = System.currentTimeMillis();
                environmentInitializer.preInitialize();
                LOGGER.debug(
                        String.format(
                                "Environment initializer %s has completed its pre-initialization in %d milliseconds",
                                environmentInitializer, System.currentTimeMillis() - start));
            } catch (Throwable t) {
                LOGGER.error(
                        String.format(
                                "Environment initializer %s has failed while pre-initializing because of %s",
                                environmentInitializer,
                                t));
            }
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                ThreadUtils.newThread(() ->
                    EnvironmentTerminatorManager.ensureTerminated(),
                        "terminator"));
    }

    private static void init() {
        for (EnvironmentInitializer environmentInitializer : environmentInitializers) {
            try {
                if (isEnvironmentInitializerIgnored(environmentInitializer)) {
                    continue;
                }
                long start = System.currentTimeMillis();
                environmentInitializer.initialize();
                LOGGER.debug(
                        String.format(
                                "Environment initializer %s has completed its initialization in %d milliseconds",
                                environmentInitializer, System.currentTimeMillis() - start));
            } catch (Throwable t) {
                LOGGER.error(
                        String.format(
                                "Environment initializer %s has failed while initializing because of %s",
                                environmentInitializer,
                        t));
            }
        }
    }

    private static CompletableFuture initAsyncIfPossible() {
        List<CompletableFuture> futures = new ArrayList<>();
        for (EnvironmentInitializer environmentInitializer : environmentInitializers) {
            try {
                if (isEnvironmentInitializerIgnored(environmentInitializer)) {
                    continue;
                }
                long start = System.currentTimeMillis();
                if (environmentInitializer instanceof EnvironmentAsyncInitializer) {
                    LOGGER.info(
                            String.format(
                                    "Initializing %s asynchronously ...",
                                    environmentInitializer));
                    ((EnvironmentAsyncInitializer) environmentInitializer).preInitializeAsync();
                    futures.add(((EnvironmentAsyncInitializer) environmentInitializer).initializeAsync());
                } else {
                    environmentInitializer.initialize();
                    LOGGER.info(
                            String.format(
                                    "Environment initializer %s has completed its initialization in %d milliseconds",
                                    environmentInitializer, System.currentTimeMillis() - start));
                }
            } catch (Throwable t) {
                LOGGER.error(
                        String.format(
                                "Environment initializer %s has failed because of %s",
                                environmentInitializer,
                        t));
            }
        }
        if (futures.size() == 0) {
            return CompletableFuture.completedFuture(null);
        } else {
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        }
    }

    private static List<EnvironmentAsyncInitializer> initNonAsyncs() {
        List<EnvironmentAsyncInitializer> asyncInitializers = new ArrayList<>();
        for (EnvironmentInitializer environmentInitializer : environmentInitializers) {
            try {
                if (isEnvironmentInitializerIgnored(environmentInitializer)) {
                    continue;
                }
                long start = System.currentTimeMillis();
                if (environmentInitializer instanceof EnvironmentAsyncInitializer) {
                    ((EnvironmentAsyncInitializer) environmentInitializer).preInitializeAsync();
                    asyncInitializers.add((EnvironmentAsyncInitializer) environmentInitializer);
                } else {
                    environmentInitializer.initialize();
                    LOGGER.info(
                            String.format(
                                    "Environment initializer %s has completed its initialization in %d milliseconds",
                                    environmentInitializer, System.currentTimeMillis() - start));
                }
            } catch (Throwable t) {
                LOGGER.error(
                        String.format(
                                "Environment initializer %s has failed because of %s",
                                environmentInitializer,
                        t));
            }
        }
        return asyncInitializers;
    }

}
