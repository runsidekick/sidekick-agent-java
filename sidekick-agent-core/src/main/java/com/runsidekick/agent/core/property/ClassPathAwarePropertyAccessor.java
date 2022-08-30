package com.runsidekick.agent.core.property;

import com.runsidekick.agent.core.util.ExceptionUtils;
import com.runsidekick.agent.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Classpath based {@link PropertyAccessor} implementation
 * which searches/loads properties from classpath.
 *
 * @author serkan
 */
public class ClassPathAwarePropertyAccessor implements PropertyAccessor {

    private final Map<String, String> props = new HashMap<>();

    public ClassPathAwarePropertyAccessor(String fileName) {
        this(fileName, ProfileProvider.getProfile());
    }

    public ClassPathAwarePropertyAccessor(String fileName, String profileName) {
        try (InputStream propertyFileStream =
                     IOUtils.getResourceAsStream(getClass().getClassLoader(), fileName)) {
            PropertyAccessorUtil.loadProperties(props, propertyFileStream, fileName);
        } catch (IOException e) {
            ExceptionUtils.sneakyThrow(e);
        }
        if (profileName != null && profileName.length() > 0) {
            String propFileName = FileUtils.getProfiledFileName(fileName, profileName);
            try (InputStream propertyFileStream =
                         IOUtils.getResourceAsStream(getClass().getClassLoader(), propFileName)) {
                if (propertyFileStream != null) {
                    PropertyAccessorUtil.loadProperties(props, propertyFileStream, propFileName);
                }
            } catch (IOException e) {
                ExceptionUtils.sneakyThrow(e);
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
