package com.runsidekick.agent.probe.sourcecode;

import com.runsidekick.agent.probe.sourcecode.provider.ClassPathSourceCodeProvider;
import com.runsidekick.agent.probe.sourcecode.provider.RootPathSourceCodeProvider;
import com.runsidekick.agent.probe.sourcecode.provider.SourceCodeProvider;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.FileUtils;
import com.runsidekick.agent.core.util.PropertyUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author serkan
 */
public final class SourceCodeSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceCodeSupport.class);

    private static final boolean SCAN_SOURCE_CODE =
            PropertyUtils.getBooleanProperty(
                    "sidekick.agent.probe.sourcecode.scan.enable", false);

    private static final List<SourceCodeProvider> sourceCodeProviders = new CopyOnWriteArrayList<>();
    private static final Map<String, SourceCodeContent> sourceCodeContentCache = new WeakHashMap();

    static {
        registerSourceCodeProvider(new ClassPathSourceCodeProvider());
        if (SCAN_SOURCE_CODE) {
            registerSourceCodeProvider(new RootPathSourceCodeProvider(FileUtils.getUserDirectoryPath()));
        }
    }

    private SourceCodeSupport() {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void registerSourceCodeProvider(SourceCodeProvider sourceCodeProvider) {
        sourceCodeProviders.add(sourceCodeProvider);
        Collections.sort(sourceCodeProviders, Comparator.comparing(SourceCodeProvider::order));
    }

    public static void deregisterSourceCodeProvider(SourceCodeProvider sourceCodeProvider) {
        sourceCodeProviders.remove(sourceCodeProvider);
    }

    public static Collection<SourceCodeProvider> listSourceCodeProviders() {
        return Collections.unmodifiableList(sourceCodeProviders);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static SourceCodeContent doGetSourceCodeContent(ClassLoader classLoader,
                                                            String className,
                                                            SourceCodeType sourceCodeType) {
        for (SourceCodeProvider sourceCodeProvider : sourceCodeProviders) {
            SourceCodeContent scc = sourceCodeProvider.getSourceCodeContent(classLoader, className, sourceCodeType);
            if (scc != null && scc != SourceCodeContent.EMPTY) {
                return scc;
            }
        }
        return SourceCodeContent.EMPTY;
    }

    public static synchronized SourceCodeContent getSourceCodeContent(ClassLoader classLoader,
                                                                      String className,
                                                                      SourceCodeType sourceCodeType) {
        SourceCodeContent sourceCodeContent = sourceCodeContentCache.get(className);
        if (sourceCodeContent == null) {
            sourceCodeContent = doGetSourceCodeContent(classLoader, className, sourceCodeType);
            sourceCodeContentCache.put(className, sourceCodeContent);
        }
        return sourceCodeContent;
    }

    public static String getSourceCode(ClassLoader classLoader,
                                       String className,
                                       SourceCodeType sourceCodeType) {
        SourceCodeContent sourceCodeContent = getSourceCodeContent(classLoader, className, sourceCodeType);
        if (sourceCodeContent == null) {
            return null;
        } else {
            return sourceCodeContent.getSource();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for(byte b : bytes) {
            result.append(Integer.toString((b & 0xFF) + 256, 16).substring(1));
        }
        return result.toString();
    }

    public static String getSourceCodeHash(ClassLoader classLoader, String className, SourceCodeType sourceCodeType)
            throws IOException {
        SourceCodeContent sourceCodeContent = getSourceCodeContent(classLoader, className, sourceCodeType);
        if (sourceCodeContent == null || !sourceCodeContent.isOriginal()) {
            return null;
        }
        String sourceCode = sourceCodeContent.getSource();
        if (sourceCode == null) {
            return null;
        }
        sourceCode = sourceCode.
                replace("\r\n", "\n").
                replace("\r\u0000\n\u0000", "\n\u0000").
                replace("\r", "\n");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sourceCode.getBytes("UTF-8"));
            return bytesToHex(hash);
        } catch (Exception e) {
            LOGGER.error("Unable to calculate hash of source code of class " + className, e);
        }
        return null;
    }

    public static String getSourceCodeHash(String className, SourceCodeType sourceCodeType) throws IOException {
        return getSourceCodeHash(ClassLoader.getSystemClassLoader(), className, sourceCodeType);
    }

}
