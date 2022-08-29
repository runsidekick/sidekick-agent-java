package com.runsidekick.agent.probe.sourcecode.provider;

import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.ClassNameUtils;
import com.runsidekick.agent.probe.sourcecode.SourceCode;
import com.runsidekick.agent.probe.sourcecode.SourceCodeContent;
import com.runsidekick.agent.probe.sourcecode.SourceCodeType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author serkan
 */
public class ClassPathSourceCodeProvider implements SourceCodeProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathSourceCodeProvider.class);

    @Override
    public int order() {
        return LOWEST;
    }

    protected String doGetSourceWithPath(ClassLoader classLoader, String className, String sourceFilePath) {
        URL sourceFileURL = classLoader.getResource(sourceFilePath);
        if (sourceFileURL == null) {
            return null;
        }
        try {
            InputStream sourceFileStream = sourceFileURL.openStream();
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

    protected SourceCodeContent doGetSourceCodeContent(ClassLoader classLoader, String className,
                                                       SourceCodeType sourceCodeType) {
        String sourceFileName = ClassNameUtils.extractSourceFileName(className, sourceCodeType.getExtension());
        String sourceFilePath = ClassNameUtils.extractSourceFilePath(className, sourceCodeType.getExtension());
        String source = doGetSourceWithPath(classLoader, className, sourceFilePath);
        if (source == null) {
            return null;
        }
        SourceCode sourceCode = new SourceCode(className, sourceFileName, sourceFilePath, sourceCodeType);
        return new SourceCodeContent(sourceCode, source);
    }

    @Override
    public SourceCodeContent getSourceCodeContent(ClassLoader classLoader,
                                                  String className, SourceCodeType sourceCodeType) {
        if (sourceCodeType != null) {
            return doGetSourceCodeContent(classLoader, className, sourceCodeType);
        } else {
            for (SourceCodeType sct : SourceCodeType.values()) {
                SourceCodeContent sourceCodeContent = doGetSourceCodeContent(classLoader, className, sct);
                if (sourceCodeContent != null) {
                    return sourceCodeContent;
                }
            }
            return null;
        }
    }

}
