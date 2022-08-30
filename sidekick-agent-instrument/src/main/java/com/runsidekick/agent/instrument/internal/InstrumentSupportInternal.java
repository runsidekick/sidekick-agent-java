package com.runsidekick.agent.instrument.internal;

import com.runsidekick.agent.core.util.ClassUtils;
import com.runsidekick.agent.core.util.IOUtils;
import com.runsidekick.agent.instrument.Installer;
import com.runsidekick.agent.instrument.InstrumentSupport;
import org.slf4j.Logger;
import com.runsidekick.agent.core.logger.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Class to implement instrumentation support internally by providing {@link Instrumentation}.
 *
 * @author serkan
 */
public class InstrumentSupportInternal {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentSupport.class);

    static {
        InstrumentSupport.checkEnabled();
    }

    public static Instrumentation attachAgent() {
        Instrumentation inst = null;

        Class<?> vmClass = null;
        Object vm = null;

        try {
            try {
                /*
                 * Use of "-Djdk.attach.allowAttachSelf" is needed to be able to attach itself on JDK 9+.
                 * So, with this hack, self attaching is enabled on JDK 9+ runtimes.
                 */
                Class hotspotVMClass = ClassUtils.getClassWithException("sun.tools.attach.HotSpotVirtualMachine");
                Field allowAttachSelfField = hotspotVMClass.getDeclaredField("ALLOW_ATTACH_SELF");
                allowAttachSelfField.setAccessible(true);

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(allowAttachSelfField, allowAttachSelfField.getModifiers() & ~Modifier.FINAL);
                allowAttachSelfField.set(hotspotVMClass, true);
            } catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
            }

            String name = "com.sun.tools.attach.VirtualMachine";
            try {
                vmClass = ClassUtils.getClassWithException(name);
            } catch (Exception e) {
                String toolsPath = System.getProperty("java.home").replace('\\', '/') + "/../lib/tools.jar";
                URL url = new File(toolsPath).toURI().toURL();
                ClassLoader classLoader = new URLClassLoader(new URL[]{url}, null);
                try {
                    vmClass = classLoader.loadClass(name);
                } catch (Exception ex) {
                }
            }

            if (vmClass != null) {
                String procId = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
                boolean vmClassProvidedByJDK = isVMClassProvidedByJDK(vmClass);
                if (vmClassProvidedByJDK) {
                    vm = vmClass.getDeclaredMethod("attach", String.class).invoke(null, procId);
                } else {
                    Class attachProviderClass = ClassUtils.getClassWithException("com.sun.tools.attach.spi.AttachProvider");
                    Class osAwareAttachProviderClass = ClassUtils.getClassWithException("sun.tools.attach.OperatingSystemAwareAttachProvider");
                    Object osAwareAttachProvider = osAwareAttachProviderClass.newInstance();
                    Class vmDescClass = ClassUtils.getClassWithException("com.sun.tools.attach.VirtualMachineDescriptor");
                    Constructor vmDescCtor = vmDescClass.getConstructor(attachProviderClass, String.class, String.class);
                    Object vmDesc = vmDescCtor.newInstance(osAwareAttachProvider, procId, "OS Aware Attach Provider");
                    vm = vmClass.getDeclaredMethod("attach", vmDescClass).invoke(null, vmDesc);
                }
                inst = loadDynamicAgent(vmClass, vm);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (vm != null) {
                try {
                    vmClass.getDeclaredMethod("detach").invoke(vm);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        return inst;
    }

    private static boolean isVMClassProvidedByJDK(Class vmClass) {
        ProtectionDomain protectionDomain = vmClass.getProtectionDomain();
        if (protectionDomain != null) {
            CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource != null) {
                String codeSourceFile = codeSource.getLocation().getFile();
                return codeSourceFile.endsWith(File.separator + "tools.jar") || codeSourceFile.endsWith("jdk.attach");
            }
        }
        return false;
    }

    private static void createAgent(File agentFile) throws Exception {
        InputStream is =
                IOUtils.getResourceAsStream(Installer.class.getClassLoader(), Installer.class.getName().replace('.', '/') + ".class");
        if (is == null) {
            LOGGER.error("No installer class!");
            return;
        }
        try {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().put(new Attributes.Name("Agent-Class"), Installer.class.getName());
            manifest.getMainAttributes().put(new Attributes.Name("Can-Redefine-Classes"), Boolean.TRUE.toString());
            manifest.getMainAttributes().put(new Attributes.Name("Can-Retransform-Classes"), Boolean.TRUE.toString());
            manifest.getMainAttributes().put(new Attributes.Name("Can-Set-Native-Method-Prefix"), Boolean.TRUE.toString());
            JarOutputStream jos = new JarOutputStream(new FileOutputStream(agentFile), manifest);
            try {
                jos.putNextEntry(new JarEntry(Installer.class.getName().replace('.', '/') + ".class"));
                byte[] buffer = new byte[1024];
                int index;
                while ((index = is.read(buffer)) != -1) {
                    jos.write(buffer, 0, index);
                }
                jos.closeEntry();
            } finally {
                jos.close();
            }
        } finally {
            is.close();
        }
    }

    private static Instrumentation loadDynamicAgent(Class vmClass, Object vm) throws Exception {
        File agentFile = null;
        try {
            agentFile = File.createTempFile("sidekick-instrument", ".jar");

            createAgent(agentFile);

            vmClass.getDeclaredMethod("loadAgent", String.class, String.class).
                    invoke(vm, agentFile.getAbsolutePath(), "");

            Field field =
                    ClassLoader.getSystemClassLoader().
                        loadClass(Installer.class.getName()).
                        getDeclaredField("INSTRUMENTATION");
            return (Instrumentation) field.get(null);
        } finally {
            if (agentFile != null) {
                agentFile.delete();
            }
        }
    }

}
