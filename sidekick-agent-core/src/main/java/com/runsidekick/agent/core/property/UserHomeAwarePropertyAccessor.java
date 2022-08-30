package com.runsidekick.agent.core.property;

/**
 * User home directory based {@link PropertyAccessor} implementation
 * which searches/loads properties from user home directory.
 *
 * @author serkan
 */
public class UserHomeAwarePropertyAccessor extends FileSystemAwarePropertyAccessor {

    public UserHomeAwarePropertyAccessor(String fileName) {
        this(fileName, ProfileProvider.getProfile());
    }

    public UserHomeAwarePropertyAccessor(String fileName, String profileName) {
        super(getUserHomeDirectory(), fileName, profileName);
    }

    private static String getUserHomeDirectory() {
        return System.getProperty("user.home");
    }

}
