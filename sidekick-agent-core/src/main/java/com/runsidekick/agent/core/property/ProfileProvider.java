package com.runsidekick.agent.core.property;

/**
 * Provider which services profile name.
 *
 * @author serkan
 */
public final class ProfileProvider {

    private ProfileProvider() {
    }

    public static String getProfile() {
        String profile = System.getProperty("sidekick.agent.property.profile");
        if (profile == null) {
            profile = System.getenv("SIDEKICK_AGENT_PROPERTY_PROFILE");
        }
        return profile;
    }

}
