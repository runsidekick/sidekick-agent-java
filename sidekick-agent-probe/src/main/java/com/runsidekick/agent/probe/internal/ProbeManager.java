package com.runsidekick.agent.probe.internal;

import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.ExceptionUtils;
import com.runsidekick.agent.instrument.InstrumentSupport;
import com.runsidekick.agent.probe.ProbeBridge;
import com.runsidekick.agent.probe.condition.Condition;
import com.runsidekick.agent.probe.domain.ClassType;
import com.runsidekick.agent.probe.domain.MutableProbe;
import com.runsidekick.agent.probe.domain.Probe;
import com.runsidekick.agent.probe.domain.ProbeAction;
import com.runsidekick.agent.probe.domain.ProbeMetadata;
import com.runsidekick.agent.probe.error.ProbeErrorCodes;
import com.runsidekick.agent.probe.sourcecode.SourceCodeSupport;
import com.runsidekick.agent.probe.sourcecode.SourceCodeType;
import com.runsidekick.agent.probe.util.ProbeUtils;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LineNumberAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author serkan
 */
public final class ProbeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProbeManager.class);

    private static final ClassPool classPool;
    private static final Instrumentation instrumentation;
    private static final Map<String, ClassProbes> classProbesMap = new ConcurrentHashMap<>();
    private static final Map<String, InternalProbe> probeMap = new ConcurrentHashMap<>();
    private static final Map<ClassLoader, Set<String>> classLoaderMap = newConcurrentWeakMap();
    private static final Set<ClassLoader> classLoaders = newConcurrentWeakSet();
    private static final ThreadLocal<TransformContext> threadLocalTransformContext = new ThreadLocal<>();
    private static boolean initialized;

    static {
        classPool = new ClassPool(null);
        registerClassloaderIfItIsNew(ProbeManager.class.getClassLoader());
        registerClassloaderIfItIsNew(ClassLoader.getSystemClassLoader());
        classPool.appendClassPath(new ClassClassPath(Object.class));

        if (ProbeManager.class.getClassLoader() != null) {
            classLoaderMap.put(ProbeManager.class.getClassLoader(), newConcurrentSet());
        }
        classLoaderMap.put(ClassLoader.getSystemClassLoader(), newConcurrentSet());

        InstrumentSupport.ensureActivated();
        instrumentation = InstrumentSupport.getInstrumentation();
        if (instrumentation == null) {
            LOGGER.error("Couldn't activate instrumentation support. So probes will not be supported");
        } else {
            instrumentation.addTransformer(new ProbeTransformer(), true);
            for (Class clazz : instrumentation.getAllLoadedClasses()) {
                ClassLoader classLoader = clazz.getClassLoader();
                if (!shouldIgnoreClassLoader(classLoader)) {
                    registerLoadedClass(classLoader, clazz.getName());
                }
            }
        }
    }

    private ProbeManager() {
    }

    //////////////////////////////////////////////////////////////////////////////////

    public synchronized static void ensureInitialized() {
        if (!initialized) {
            doInitialize();
            initialized = true;
        }
    }

    private static void doInitialize() {
        // TODO initialization logic if there is
        // Currently all initialization logic is
        // in the static initializer of this class.
        // So once this class is touched first time,
        // all the initialization logic is executed.
    }

    private static <K, V> Map<K, V> newConcurrentWeakMap() {
        return Collections.synchronizedMap(new WeakHashMap<>());
    }

    private static <V> Set<V> newConcurrentSet() {
        return Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    private static <V> Set<V> newConcurrentWeakSet() {
        return Collections.newSetFromMap(newConcurrentWeakMap());
    }

    private static boolean shouldIgnoreClassLoader(ClassLoader classLoader) {
        // Check whether it is bootstrap classloader or extension classloader
        return classLoader == null || classLoader == ClassLoader.getSystemClassLoader().getParent();
    }

    private static void registerLoadedClass(ClassLoader classLoader, String className) {
        if (shouldIgnoreClassLoader(classLoader)) {
            return;
        }
        Set<String> classNames = classLoaderMap.get(classLoader);
        if (classNames == null) {
            classNames = newConcurrentSet();
            Set<String> existingClassNames = classLoaderMap.putIfAbsent(classLoader, classNames);
            if (existingClassNames != null) {
                classNames = existingClassNames;
            }
        }
        classNames.add(className);
    }

    private static class TransformContext {

        private Throwable error;

    }

    private static class ProbeTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            try {
                if (className == null || shouldIgnoreClassLoader(loader)) {
                    return null;
                }
                className = className.replace("/", ".");

                registerLoadedClass(loader, className);

                String normalizeClassName = normalizeClassName(className);
                ClassProbes classProbes = classProbesMap.get(normalizeClassName);
                if (classProbes != null) {
                    return instrumentProbes(className, loader, classfileBuffer, classProbes);
                }
            } catch (Throwable error) {
                TransformContext transformContext = threadLocalTransformContext.get();
                if (transformContext != null) {
                    transformContext.error = error;
                } else {
                    LOGGER.error(String.format("Error occurred while transforming class %s", className), error);
                }
            }
            return null;
        }

    }

    private static String normalizeClassName(String className) {
        int idx = className.indexOf("$");
        if (idx > 0) {
            return className.substring(0, idx);
        } else {
            return className;
        }
    }

    private static boolean reloadClassIfAlreadyLoaded(String className) throws Throwable {
        List<Class> classesToReload = new ArrayList<>();
        for (Map.Entry<ClassLoader, Set<String>> e : classLoaderMap.entrySet()) {
            ClassLoader classLoader = e.getKey();
            Set<String> classNames = e.getValue();
            if (classNames.contains(className)) {
                try {
                    Class clazz = classLoader.loadClass(className);
                    classesToReload.add(clazz);
                } catch (ClassNotFoundException ex) {
                    LOGGER.error("Unable to load class {} by classloader", className, classLoader);
                }
            }
        }
        if (!classesToReload.isEmpty()) {
            TransformContext transformContext = new TransformContext();
            threadLocalTransformContext.set(transformContext);
            try {
                instrumentation.retransformClasses(classesToReload.toArray(new Class[classesToReload.size()]));
                if (transformContext.error != null) {
                    throw transformContext.error;
                }
            } finally {
                threadLocalTransformContext.remove();
            }
            return true;
        }
        return false;
    }

    private static ClassLoader getClassLoader(String className) {
        // First lookup in the already loaded classes
        for (Map.Entry<ClassLoader, Set<String>> e : classLoaderMap.entrySet()) {
            ClassLoader classLoader = e.getKey();
            Set<String> classNames = e.getValue();
            if (classNames.contains(className)) {
                return classLoader;
            }
        }

        // Then, find a classloader which is able to load the class, but don't load at this time
        String classResourceName = className.replace('.', '/').concat(".class");
        for (ClassLoader classLoader : classLoaderMap.keySet()) {
            if (classLoader.getResource(classResourceName) != null) {
                return classLoader;
            }
        }

        return null;
    }

    private static void registerClassloaderIfItIsNew(ClassLoader classLoader) {
        if (classLoader != null && classLoaders.add(classLoader)) {
            classPool.appendClassPath(new LoaderClassPath(classLoader));
        }
    }

    private static CtMethod getMethodAtLine(CtClass clazz, int lineNo) throws NotFoundException {
        return getMethodAtLine(clazz, lineNo, null);
    }

    private static CtMethod getMethodAtLine(CtClass clazz, int lineNo,
                                            AtomicReference<CtMethod> ownerMethodRef) throws NotFoundException {
        for (CtMethod method : clazz.getDeclaredMethods()) {
            MethodInfo methodInfo = method.getMethodInfo();
            CodeAttribute codeAttr = methodInfo.getCodeAttribute();
            if (codeAttr == null) {
                continue;
            }
            int byteCodeLength = codeAttr.getCode().length;
            int startLine = methodInfo.getLineNumber(0);
            int endLine = methodInfo.getLineNumber(byteCodeLength - 1);
            if (lineNo >= startLine && lineNo <= endLine) {
                if (ownerMethodRef != null) {
                    ownerMethodRef.set(method);
                }
                LineNumberAttribute lineNoAttr =
                        (LineNumberAttribute) codeAttr.getAttribute(LineNumberAttribute.tag);
                if (lineNoAttr == null) {
                    continue;
                }
                LineNumberAttribute.Pc nearPc = lineNoAttr.toNearPc(lineNo);
                if (nearPc.line == lineNo) {
                    return method;
                }
            }
        }
        for (CtClass nestedClazz : clazz.getNestedClasses()) {
            CtMethod method = getMethodAtLine(nestedClazz, lineNo);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    private static void checkWhetherLineIsAvailable(String className, CtMethod method, int lineNo) {
        MethodInfo methodInfo = method.getMethodInfo();
        CodeAttribute codeAttr = methodInfo.getCodeAttribute();
        if (codeAttr == null) {
            String errorMessage =
                    String.format(
                            "No code info could be found in class %s. So probes are not supported",
                            className);
            LOGGER.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        LineNumberAttribute lineNoAttr =
                (LineNumberAttribute) codeAttr.getAttribute(LineNumberAttribute.tag);
        if (lineNoAttr == null) {
            String errorMessage =
                    String.format(
                            "No line number info could be found in class %s. So probes are not supported",
                            className);
            LOGGER.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        LineNumberAttribute.Pc nearPc = lineNoAttr.toNearPc(lineNo);
        if (nearPc.line != lineNo) {
            LOGGER.error("Line number {} in class {} is not available", lineNo, className);
            throw new CodedException(ProbeErrorCodes.LINE_NO_IS_NOT_AVAILABLE, lineNo, className);
        }
    }

    private static String generateMethodId(CtMethod method) {
        return method.getDeclaringClass().getName() + "." + method.getLongName();
    }

    private static CtMethod getMethod(List<CtMethod> methods, String methodId) {
        for (CtMethod method : methods) {
            if (methodId.equals(generateMethodId(method))) {
                return method;
            }
        }
        return null;
    }

    private static byte[] instrumentProbes(String className,
                                           ClassLoader classLoader,
                                           byte[] classByteCode,
                                           ClassProbes classProbes)
            throws CannotCompileException, IOException {
        CtClass clazz = classPool.makeClass(new ByteArrayInputStream(classByteCode));
        try {
            ClassType classType = ProbeUtils.getClassType(clazz);
            List<CtMethod> methods = Arrays.asList(clazz.getDeclaredMethods());

            for (MethodProbes methodProbes : classProbes.methodProbesMap.values()) {
                String methodId = methodProbes.methodId;
                CtMethod method = getMethod(methods, methodId);
                if (method == null) {
                    continue;
                }
                instrumentMethodProbes(className, classLoader, clazz, classType, method, methodProbes);
            }

            return clazz.toBytecode();
        } finally {
            clazz.defrost();
        }
    }

    private static void instrumentMethodProbes(String className,
                                               ClassLoader classLoader,
                                               CtClass clazz,
                                               ClassType classType,
                                               CtMethod method,
                                               MethodProbes methodProbes) {
        MethodInfo methodInfo = method.getMethodInfo();
        CodeAttribute codeAttr = methodInfo.getCodeAttribute();
        if (codeAttr == null) {
            String errorMessage =
                    String.format(
                            "No code info could be found in class %s. So probes are not supported",
                            className);
            LOGGER.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        LineNumberAttribute lineNoAttr =
                (LineNumberAttribute) codeAttr.getAttribute(LineNumberAttribute.tag);
        if (lineNoAttr == null) {
            String errorMessage =
                    String.format(
                            "No line number info could be found in class %s. So probes are not supported",
                            className);
            LOGGER.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        LocalVariableAttribute localVarAttr =
                (LocalVariableAttribute) codeAttr.getAttribute(LocalVariableAttribute.tag);
        if (localVarAttr == null) {
            String errorMessage =
                    String.format(
                            "No local variable info could be found in class %s. So probes are not supported",
                            className);
            LOGGER.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        for (InternalProbe probe : methodProbes.probes.values()) {
            Collection<LocalVarMetadata> localVarMetadatas =
                    extractLocalVarMetadata(classType, lineNoAttr, localVarAttr, probe.getLineNo());
            String localVariableTraceExpression = generateLocalVariableTraceExpression(localVarMetadatas);
            String probeExpression =
                    ProbeBridge.class.getName() + ".onProbe" +
                            "(" +
                                "\"" + probe.getId() + "\"" + ", " +
                                clazz.getName() + ".class" + ", " +
                                (Modifier.isStatic(method.getModifiers()) ? "null" : "$0") + ", " +
                                localVariableTraceExpression +
                            ");";
            try {
                method.insertAt(probe.getLineNo(), probeExpression);
            } catch (CannotCompileException e) {
                LOGGER.error("Line number {} in class {} is not available", probe.getLineNo(), className);
                throw new CodedException(ProbeErrorCodes.LINE_NO_IS_NOT_AVAILABLE, probe.getLineNo(), className);
            }
        }
    }

    private static String generateLocalVariableTraceExpression(Collection<LocalVarMetadata> localVarMetadataList) {
        StringBuilder localVarNameBuilder = new StringBuilder("new String[] { ");
        StringBuilder localVarValueBuilder = new StringBuilder("new Object[] { ");
        boolean localVarTraced = false;
        if (localVarMetadataList != null && !localVarMetadataList.isEmpty()) {
            for (LocalVarMetadata localVarMetadata : localVarMetadataList) {
                if (localVarTraced) {
                    localVarNameBuilder.append(", ");
                    localVarValueBuilder.append(", ");
                }
                localVarNameBuilder.append("\"" + localVarMetadata.getOriginalName() + "\"");
                localVarValueBuilder.append("($w) " + localVarMetadata.getName());
                localVarTraced = true;
            }
        }
        if (!localVarTraced) {
            return "new String[0], new Object[0]";
        }
        localVarNameBuilder.append(" }");
        localVarValueBuilder.append(" }");
        return localVarNameBuilder.toString() + ", " + localVarValueBuilder.toString();
    }

    static String generateProbeId(String className, int lineNo, String client) {
        return className + "::" + lineNo + "::" + client;
    }

    static Collection<LocalVarMetadata> extractLocalVarMetadata(ClassType classType,
                                                                LineNumberAttribute lineNoAttr,
                                                                LocalVariableAttribute localVarAttr,
                                                                int line) {
        int lineStartPc = lineNoAttr.toStartPc(line);
        Set<LocalVarMetadata> localVarMetadatas = new TreeSet<>();
        if (lineStartPc >= 0) {
            for (int i = 0; i < localVarAttr.tableLength(); i++) {
                int localVarStartPc = localVarAttr.startPc(i);
                int localVarEndPc = localVarStartPc + localVarAttr.codeLength(i);
                if (localVarStartPc <= lineStartPc && localVarEndPc > lineStartPc) {
                    String localVariableName = localVarAttr.variableName(i);
                    if ("this".equals(localVariableName) || "$this".equals(localVariableName)) {
                        continue;
                    }
                    if (localVariableName != null && !localVariableName.startsWith("___")) {
                        String originalLocalVariableName = localVariableName;
                        if (classType == ClassType.SCALA) {
                            int dollarIdx = localVariableName.indexOf("$");
                            if (dollarIdx > 0) {
                                originalLocalVariableName = originalLocalVariableName.substring(0, dollarIdx);
                            }
                        }
                        LocalVarMetadata localVarMetadata =
                                new LocalVarMetadata(localVarStartPc, i, localVariableName,
                                        originalLocalVariableName, localVarAttr.signature(i));
                        localVarMetadatas.add(localVarMetadata);
                    }
                }
            }
        }
        return localVarMetadatas;
    }

    //////////////////////////////////////////////////////////////////////////////////

    public static <P extends Probe> P getProbe(String probeId) {
        return (P) probeMap.get(probeId);
    }

    public static <P extends Probe> P getProbe(String className, int lineNo, String client) {
        className = normalizeClassName(className);
        String probeId = generateProbeId(className, lineNo, client);
        return (P) probeMap.get(probeId);
    }

    public static ProbeMetadata getProbeMetadata(String className, int lineNo, String client) throws Exception {
        ClassLoader classLoader = getClassLoader(className);
        if (classLoader == null) {
            LOGGER.error("Unable to find class {}", className);
            throw new CodedException(ProbeErrorCodes.UNABLE_TO_FIND_CLASS, className);
        }
        registerClassloaderIfItIsNew(classLoader);

        CtClass clazz;
        try {
            clazz = classPool.getCtClass(className);
        } catch (NotFoundException e) {
            LOGGER.error("Unable to find class {}", className);
            throw new CodedException(ProbeErrorCodes.UNABLE_TO_FIND_CLASS, className);
        }

        AtomicReference<CtMethod> ownerMethodRef = new AtomicReference<>();
        CtMethod method = getMethodAtLine(clazz, lineNo, ownerMethodRef);
        if (method == null) {
            if (ownerMethodRef.get() != null) {
                LOGGER.error("Line number {} in class {} is not available", lineNo, className);
                throw new CodedException(ProbeErrorCodes.LINE_NO_IS_NOT_AVAILABLE, lineNo, className);
            } else {
                LOGGER.error(
                        "No method could be found in class {} on line {} from client {} to put probe",
                        className, lineNo, client);
                throw new CodedException(ProbeErrorCodes.NO_METHOD_COULD_BE_FOUND, className, lineNo, client);
            }
        }

        ClassType classType = ProbeUtils.getClassType(clazz);

        checkWhetherLineIsAvailable(className, method, lineNo);

        return new ProbeMetadata(classLoader, clazz, method, classType);
    }

    public static ProbeMetadata getProbeMetadata(Probe probe) throws Exception {
        return getProbeMetadata(probe.getClassName(), probe.getLineNo(), probe.getClient());
    }

    public static synchronized InternalProbe getOrPutProbe(String fileName, String className, int lineNo, String client) {
        className = normalizeClassName(className);

        LOGGER.debug("Putting probe with id {} to class {} on line {} from client {}", className, lineNo, client);

        if (instrumentation == null) {
            throw new CodedException(ProbeErrorCodes.INSTRUMENTATION_IS_NOT_ACTIVE);
        }

        boolean instrumented = false;
        String methodId = null;
        MethodProbes methodProbes = null;
        InternalProbe probe = null;
        boolean added = false;

        try {
            ProbeMetadata metadata = getProbeMetadata(className, lineNo, client);
            CtClass clazz = metadata.clazz();
            CtMethod method = metadata.method();
            ClassType classType = metadata.classType();

            try {
                methodId = generateMethodId(method);

                ClassProbes classProbes = classProbesMap.get(className);
                if (classProbes == null) {
                    classProbes = new ClassProbes();
                    classProbesMap.put(className, classProbes);
                }
                methodProbes = classProbes.methodProbesMap.get(methodId);
                if (methodProbes == null) {
                    methodProbes = new MethodProbes(classProbes, methodId);
                    classProbes.methodProbesMap.put(methodId, methodProbes);
                }
                String ownerClassName = method.getDeclaringClass().getName();
                probe = new InternalProbe(
                        methodProbes, ownerClassName, classType,
                        generateProbeId(className, lineNo, client),
                        fileName, className, lineNo, client, metadata, methodId);
                InternalProbe existingProbe = methodProbes.probes.putIfAbsent(probe.getId(), probe);
                added = existingProbe == null;
                if (!added) {
                    LOGGER.debug(
                            "Probe has been already added in class {} on line {} from client {}",
                            className, lineNo, client);
                    return existingProbe;
                }
                probeMap.put(probe.getId(), probe);

                String classNameToReload = method.getDeclaringClass().getName();
                instrumented = reloadClassIfAlreadyLoaded(classNameToReload);

                return probe;
            } finally {
                clazz.defrost();
                clazz.detach();

                LOGGER.debug("Completed putting probe to class {} on line {} from client {}", className, lineNo, client);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("Unable to find class {}", className);
            throw new CodedException(ProbeErrorCodes.UNABLE_TO_FIND_CLASS, className);
        } catch (Throwable t) {
            if (added && !instrumented && methodId != null && methodProbes != null && probe != null) {
                // Revert
                methodProbes.probes.remove(probe.getId());
                probeMap.remove(probe.getId());
                if (methodProbes.probes.size() == 0) {
                    methodProbes.ownerClassProbes.methodProbesMap.remove(methodId);
                }
            }
            LOGGER.error(
                    "Error occurred while putting probe to class {} on line {} from client {}: {}",
                    className, lineNo, client, t.getMessage());
            return ExceptionUtils.sneakyThrow(t);
        }
    }

    public static synchronized void removeProbe(String id, boolean ifEmpty) {
        LOGGER.debug("Removing probe with id {}", id);

        if (instrumentation == null) {
            throw new CodedException(ProbeErrorCodes.INSTRUMENTATION_IS_NOT_ACTIVE);
        }

        InternalProbe probe = probeMap.get(id);
        if (probe == null) {
            LOGGER.error(
                    "No probe could be found with id {}",
                    id);
            throw new CodedException(ProbeErrorCodes.NO_PROBE_EXIST, id);
        }

        if (ifEmpty && probe.hasAnyAction()) {
            LOGGER.debug("Probe with id {} has action so skipping removing", id);
            return;
        }

        probeMap.remove(id);

        boolean uninstrumented = false;
        String methodId = probe.methodId;
        MethodProbes methodProbes = probe.ownerMethodProbes;
        ClassProbes classProbes = methodProbes.ownerClassProbes;
        boolean removed = false;

        try {
            removed = methodProbes.probes.remove(id) != null;

            if (methodProbes.probes.size() == 0) {
                classProbes.methodProbesMap.remove(methodId);
            }

            uninstrumented = reloadClassIfAlreadyLoaded(probe.ownerClassName);

            probe.setRemoved(true);
        } catch (Throwable t) {
            if (removed && !uninstrumented && methodId != null && methodProbes != null && probe != null) {
                // Add back
                ClassProbes ownerClassProbes = methodProbes.ownerClassProbes;
                methodProbes = ownerClassProbes.methodProbesMap.get(methodId);
                if (methodProbes == null) {
                    methodProbes = new MethodProbes(ownerClassProbes, methodId);
                    ownerClassProbes.methodProbesMap.put(methodId, methodProbes);
                }
                methodProbes.probes.put(id, probe);
                probeMap.put(id, probe);
            }
            LOGGER.error(
                    "Error occurred while removing probe with id {}: {}",
                    id, t.getMessage());
            ExceptionUtils.sneakyThrow(t);
        }
    }

    public static Condition getCondition(String conditionExpression,
                                         String className, ClassLoader classLoader, ClassType classType,
                                         CtMethod method, int lineNo) {
        return ConditionHelper.getCondition(conditionExpression, className, classLoader, classType, method, lineNo);
    }

    public static String getSourceCodeHash(ClassLoader classLoader, CtClass clazz,
                                           ClassType classType, String className) throws IOException {
        String sourceFileExtension = classType.getExtension();
        SourceCodeType sourceCodeType = SourceCodeType.fromExtension(sourceFileExtension);
        return SourceCodeSupport.getSourceCodeHash(classLoader, className, sourceCodeType);
    }

    public static <A extends ProbeAction> A addProbeAction(Probe probe, A action) {
        if (!(probe instanceof MutableProbe)) {
            throw new IllegalArgumentException("Not an " + MutableProbe.class.getName());
        }
        MutableProbe mutableProbe = (MutableProbe) probe;
        return mutableProbe.addAction(action);
    }

    public static <A extends ProbeAction> A replaceProbeAction(Probe probe, A action) {
        if (!(probe instanceof MutableProbe)) {
            throw new IllegalArgumentException("Not an " + MutableProbe.class.getName());
        }
        MutableProbe mutableProbe = (MutableProbe) probe;
        return mutableProbe.replaceAction(action);
    }

    public static <A extends ProbeAction> A removeProbeAction(Probe probe, String id) {
        if (!(probe instanceof MutableProbe)) {
            throw new IllegalArgumentException("Not an " + MutableProbe.class.getName());
        }
        MutableProbe mutableProbe = (MutableProbe) probe;
        return mutableProbe.removeAction(id);
    }

}
