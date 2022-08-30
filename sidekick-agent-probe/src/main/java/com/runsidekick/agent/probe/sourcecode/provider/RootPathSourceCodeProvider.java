package com.runsidekick.agent.probe.sourcecode.provider;

import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.ClassNameUtils;
import com.runsidekick.agent.core.util.FileUtils;
import com.runsidekick.agent.probe.sourcecode.SourceCode;
import com.runsidekick.agent.probe.sourcecode.SourceCodeContent;
import com.runsidekick.agent.probe.sourcecode.SourceCodeType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author serkan
 */
public class RootPathSourceCodeProvider implements SourceCodeProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootPathSourceCodeProvider.class);

    private static final String[] EXTENSIONS = new String[] {
            SourceCodeType.JAVA.getExtension(),
            SourceCodeType.KOTLIN.getExtension(),
            SourceCodeType.SCALA.getExtension()
    };

    private final String rootPath;
    private final Map<String, SourceCode> sourceCodeMap;

    public RootPathSourceCodeProvider() {
        this(FileUtils.getUserDirectoryPath());
    }

    public RootPathSourceCodeProvider(String rootPath) {
        this.rootPath = rootPath;
        this.sourceCodeMap = populateSourceCodes(rootPath);
    }

    public String getRootPath() {
        return rootPath;
    }

    private Map<String, SourceCode> populateSourceCodes(String rootPath) {
        long start = System.currentTimeMillis();
        Map<String, SourceCode> sourceCodeByClassName = new HashMap();
        Map<String, Set<String>> conflictFilePathsByClassName = new HashMap();
        if (StringUtils.isEmpty(rootPath)) {
            LOGGER.debug("Root path is empty");
            return sourceCodeByClassName;
        } else {
            try {
                File rootDir = new File(rootPath);
                List<File> files = (List) FileUtils.listFiles(rootDir, EXTENSIONS, true);
                for (File file : files) {
                    String className = ClassNameUtils.extractClassNameFromSource(file);
                    try {
                        if (!className.endsWith("package-info") && !className.endsWith("module-info")) {
                            String sourceFileExtension = FilenameUtils.getExtension(file.getName());
                            String sourceFileName = ClassNameUtils.extractSourceFileName(className, sourceFileExtension);
                            String sourceFilePath = file.getCanonicalPath();
                            SourceCodeType sourceCodeType = SourceCodeType.fromExtension(sourceFileExtension);
                            SourceCode sourceCode =
                                    new SourceCode(className, sourceFileName, sourceFilePath, sourceCodeType);
                            if (!sourceCode.hasSourceCodeFilePath()
                                    || (!sourceCode.getSourceFilePath().contains("target/checkout")
                                    && !sourceCode.getSourceFilePath().contains("target/munged"))) {
                                SourceCode existingSourceCode = sourceCodeByClassName.putIfAbsent(className, sourceCode);
                                if (existingSourceCode != null) {
                                    sourceCodeByClassName.put(className, sourceCode);
                                    Set<String> conflicts = conflictFilePathsByClassName.get(className);
                                    if (conflicts == null) {
                                        conflicts = new HashSet<>();
                                        conflictFilePathsByClassName.put(className, conflicts);
                                    }
                                    conflicts.add(sourceCode.getSourceFilePath());
                                    conflicts.add(existingSourceCode.getSourceFilePath());
                                }
                            }
                        }
                    } catch (Throwable t) {
                        LOGGER.error("Error occurred while getting source code of class " + className, t);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while getting source code source Codes map", e);
            }
            if (!conflictFilePathsByClassName.isEmpty()) {
                for (Map.Entry<String, Set<String>> e : conflictFilePathsByClassName.entrySet()) {
                    String className = e.getKey();
                    Set<String> conflicts = e.getValue();
                    LOGGER.warn("Source code conflict for class {}: {}", className, conflicts);
                }
            }
            LOGGER.debug("Completed source code populating under root path '{}' in {} milliseconds",
                    rootPath, (System.currentTimeMillis() - start));
            return sourceCodeByClassName;
        }
    }

    @Override
    public int order() {
        return HIGH;
    }

    protected String doGetSourceWithPath(String className, String sourceFilePath) {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            return null;
        }
        try {
            InputStream sourceFileStream = new FileInputStream(sourceFile);
            return IOUtils.toString(sourceFileStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error(
                    String.format(
                            "Unable to get source code for class '%s' from path '%s'",
                            className, sourceFilePath),
                    e);
            return null;
        }
    }

    protected String doGetSource(String className, SourceCode sourceCode) {
        if (sourceCode != null) {
            return doGetSourceWithPath(className, sourceCode.getSourceFilePath());
        }
        return null;
    }

    @Override
    public SourceCodeContent getSourceCodeContent(ClassLoader classLoader,
                                                  String className,
                                                  SourceCodeType sourceCodeType) {
        SourceCodeContent sourceCodeContent;
        SourceCode sourceCode = sourceCodeMap.get(className);
        String source = doGetSource(className, sourceCode);
        if (sourceCode == null && source == null) {
            sourceCodeContent = SourceCodeContent.EMPTY;
        } else {
            sourceCodeContent = new SourceCodeContent(sourceCode, source);
        }
        return sourceCodeContent;
    }

}
