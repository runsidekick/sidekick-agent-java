package com.runsidekick.agent.core.property;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author serkan
 */
public class PropertyAccessorTest {

    @Test
    public void systemPropertyAwarePropertyAccessorShouldProvidePropertySuccessfully() {
        System.setProperty("key1", "value1");
        try {
            SystemPropertyAwarePropertyAccessor propertyAccessor =
                    new SystemPropertyAwarePropertyAccessor();
            assertEquals("value1", propertyAccessor.getProperty("key1"));
            assertEquals("value1", propertyAccessor.getProperties().get("key1"));
        } finally {
            System.clearProperty("key1");
        }
    }

    @Test
    public void systemEnvironmentAwarePropertyAccessorShouldProvidePropertySuccessfully() {
        PropertyAccessorTestUtil.setEnvironmentVariable("key1", "value1");
        try {
            SystemEnvironmentAwarePropertyAccessor propertyAccessor =
                    new SystemEnvironmentAwarePropertyAccessor();
            assertEquals("value1", propertyAccessor.getProperty("key1"));
            assertEquals("value1", propertyAccessor.getProperties().get("key1"));
        } finally {
            PropertyAccessorTestUtil.resetEnvironmentVariables();
        }
    }

    @Test
    public void classpathAwarePropertyAccessorShouldProvidePropertySuccessfullyFromPropertiesFile() {
        ClassPathAwarePropertyAccessor propertyAccessor =
                new ClassPathAwarePropertyAccessor("test.properties");
        assertEquals("value1", propertyAccessor.getProperty("test.key1"));
        assertEquals("value1", propertyAccessor.getProperties().get("test.key1"));
    }

    @Test
    public void classpathAwarePropertyAccessorShouldProvidePropertySuccessfullyFromYAMLFile() {
        ClassPathAwarePropertyAccessor propertyAccessor =
                new ClassPathAwarePropertyAccessor("test.yml");
        assertEquals("value1", propertyAccessor.getProperty("test.key1"));
        assertEquals("value1", propertyAccessor.getProperties().get("test.key1"));
    }

    @Test
    public void classpathAwarePropertyAccessorWithProfileShouldProvidePropertySuccessfullyFromPropertiesFile() {
        ClassPathAwarePropertyAccessor propertyAccessor =
                new ClassPathAwarePropertyAccessor("test.properties", "testprofile");
        assertEquals("value11", propertyAccessor.getProperty("test.key1"));
        assertEquals("value11", propertyAccessor.getProperties().get("test.key1"));
    }

    @Test
    public void classpathAwarePropertyAccessorWithProfileShouldProvidePropertySuccessfullyFromYAMLFile() {
        ClassPathAwarePropertyAccessor propertyAccessor =
                new ClassPathAwarePropertyAccessor("test.yml", "testprofile");
        assertEquals("value11", propertyAccessor.getProperty("test.key1"));
        assertEquals("value11", propertyAccessor.getProperties().get("test.key1"));
    }

    @Test
    public void fileSystemAwarePropertyAccessorShouldProvidePropertySuccessfullyFromPropertiesFile() {
        String baseDir = getClass().getClassLoader().getResource("").getFile();
        FileSystemAwarePropertyAccessor propertyAccessor =
                new FileSystemAwarePropertyAccessor(baseDir, "test.properties");
        assertEquals("value1", propertyAccessor.getProperty("test.key1"));
        assertEquals("value1", propertyAccessor.getProperties().get("test.key1"));
    }

    @Test
    public void fileSystemAwarePropertyAccessorShouldProvidePropertySuccessfullyFromYAMLFile() {
        String baseDir = getClass().getClassLoader().getResource("").getFile();
        FileSystemAwarePropertyAccessor propertyAccessor =
                new FileSystemAwarePropertyAccessor(baseDir, "test.yml");
        assertEquals("value1", propertyAccessor.getProperty("test.key1"));
        assertEquals("value1", propertyAccessor.getProperties().get("test.key1"));
    }

    @Test
    public void fileSystemAwarePropertyAccessorWithProfileShouldProvidePropertySuccessfullyFromPropertiesFile() {
        String baseDir = getClass().getClassLoader().getResource("").getFile();
        FileSystemAwarePropertyAccessor propertyAccessor =
                new FileSystemAwarePropertyAccessor(baseDir, "test.properties", "testprofile");
        assertEquals("value11", propertyAccessor.getProperty("test.key1"));
        assertEquals("value11", propertyAccessor.getProperties().get("test.key1"));
    }

    @Test
    public void fileSystemAwarePropertyAccessorWithProfileShouldProvidePropertySuccessfullyFromYAMLFile() {
        String baseDir = getClass().getClassLoader().getResource("").getFile();
        FileSystemAwarePropertyAccessor propertyAccessor =
                new FileSystemAwarePropertyAccessor(baseDir, "test.yml", "testprofile");
        assertEquals("value11", propertyAccessor.getProperty("test.key1"));
        assertEquals("value11", propertyAccessor.getProperties().get("test.key1"));
    }

    @Test
    public void userHomeAwarePropertyAccessorShouldProvidePropertySuccessfullyFromPropertiesFile() {
        String baseDir = getClass().getClassLoader().getResource("").getFile();
        String userHome = System.getProperty("user.home");
        System.setProperty("user.home", baseDir);
        try {
            UserHomeAwarePropertyAccessor propertyAccessor =
                    new UserHomeAwarePropertyAccessor("test.properties");
            assertEquals("value1", propertyAccessor.getProperty("test.key1"));
            assertEquals("value1", propertyAccessor.getProperties().get("test.key1"));
        } finally {
            System.setProperty("user.home", userHome);
        }
    }

    @Test
    public void userHomeAwarePropertyAccessorShouldProvidePropertySuccessfullyFromYAMLFile() {
        String baseDir = getClass().getClassLoader().getResource("").getFile();
        String userHome = System.getProperty("user.home");
        System.setProperty("user.home", baseDir);
        try {
            UserHomeAwarePropertyAccessor propertyAccessor =
                    new UserHomeAwarePropertyAccessor("test.yml");
            assertEquals("value1", propertyAccessor.getProperty("test.key1"));
            assertEquals("value1", propertyAccessor.getProperties().get("test.key1"));
        } finally {
            System.setProperty("user.home", userHome);
        }
    }

    @Test
    public void userHomeAwarePropertyAccessorWithProfileShouldProvidePropertySuccessfullyFromPropertiesFile() {
        String baseDir = getClass().getClassLoader().getResource("").getFile();
        String userHome = System.getProperty("user.home");
        System.setProperty("user.home", baseDir);
        try {
            UserHomeAwarePropertyAccessor propertyAccessor =
                    new UserHomeAwarePropertyAccessor("test.properties", "testprofile");
            assertEquals("value11", propertyAccessor.getProperty("test.key1"));
            assertEquals("value11", propertyAccessor.getProperties().get("test.key1"));
        } finally {
            System.setProperty("user.home", userHome);
        }
    }

    @Test
    public void userHomeAwarePropertyAccessorWithProfileShouldProvidePropertySuccessfullyFromYAMLFile() {
        String baseDir = getClass().getClassLoader().getResource("").getFile();
        String userHome = System.getProperty("user.home");
        System.setProperty("user.home", baseDir);
        try {
            UserHomeAwarePropertyAccessor propertyAccessor =
                    new UserHomeAwarePropertyAccessor("test.yml", "testprofile");
            assertEquals("value11", propertyAccessor.getProperty("test.key1"));
            assertEquals("value11", propertyAccessor.getProperties().get("test.key1"));
        } finally {
            System.setProperty("user.home", userHome);
        }
    }

    @Test
    public void providedPropertyAccessorShouldProvidePropertySuccessfully() {
        ProvidedPropertyAccessor propertyAccessor =
                ProvidedPropertyAccessor.INSTANCE;
        assertEquals("value1", propertyAccessor.getProperty("key1"));
        assertNull(null, propertyAccessor.getProperty("key2"));
    }

    @Test
    public void combinedPropertyAccessorShouldProvidePropertySuccessfully() {
        Map<String, String> props1 = new HashMap<String, String>();
        PropertyAccessor propertyAccessor1 =
                new PropertyAccessor() {
                    @Override
                    public String getProperty(String propName) {
                        return props1.get(propName);
                    }

                    @Override
                    public Map<String, String> getProperties() {
                        return props1;
                    }
                };
        Map<String, String> props2 = new HashMap<String, String>();
        PropertyAccessor propertyAccessor2 =
                new PropertyAccessor() {
                    @Override
                    public String getProperty(String propName) {
                        return props2.get(propName);
                    }

                    @Override
                    public Map<String, String> getProperties() {
                        return props2;
                    }
                };
        props1.put("key1", "value1");
        props1.put("key2", "value2");
        props2.put("key2", "value22");
        props2.put("key3", "value3");

        CombinedPropertyAccessor propertyAccessor =
                CombinedPropertyAccessor.
                        builder().
                            add(propertyAccessor2).
                            add(propertyAccessor1).
                        build();
        assertEquals("value1", propertyAccessor.getProperty("key1"));
        assertEquals("value22", propertyAccessor.getProperty("key2"));
        assertEquals("value3", propertyAccessor.getProperty("key3"));
    }

}
