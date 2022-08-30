package com.runsidekick.agent.core.property;

import com.runsidekick.agent.core.util.ExceptionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * File system based {@link PropertyAccessor} implementation
 * which searches/loads properties from file system.
 *
 * @author serkan
 */
public class FileSystemAwarePropertyAccessor implements PropertyAccessor {

    private final Map<String, String> props = new HashMap<>();

    public FileSystemAwarePropertyAccessor(String dirPath, String fileName) {
        this(dirPath, fileName, ProfileProvider.getProfile());
    }

    public FileSystemAwarePropertyAccessor(String dirPath, String fileName, String profileName) {
        File propertyFile = new File(dirPath + File.separator + fileName);
        if (propertyFile.exists()) {
            try (FileInputStream propertyFileStream = new FileInputStream(propertyFile)) {
                PropertyAccessorUtil.loadProperties(props, propertyFileStream, fileName);
            } catch (IOException e) {
                ExceptionUtils.sneakyThrow(e);
            }
        }
        if (profileName != null && profileName.length() > 0) {
            String propFileName = FileUtils.getProfiledFileName(fileName, profileName);
            propertyFile = new File(dirPath + File.separator + propFileName);
            if (propertyFile.exists()) {
                try (FileInputStream propertyFileStream = new FileInputStream(propertyFile)) {
                    PropertyAccessorUtil.loadProperties(props, propertyFileStream, propFileName);
                } catch (IOException e) {
                    ExceptionUtils.sneakyThrow(e);
                }
            }
        }
    }

    @Override
    public String getProperty(String propName) {
        return props.get(propName);
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(props);
    }

}
