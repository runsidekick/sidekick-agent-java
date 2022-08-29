package com.runsidekick.agent.core.internal;

import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.PropertyUtils;
import org.slf4j.Logger;

/**
 * Utility class for checking features.
 *
 * @author serkan
 */
public final class FeatureChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger("FeatureChecker");

    private FeatureChecker() {
    }

    public static boolean isFeatureEnabled(String featureName, int level) {
        boolean enabled = false;
        try {
            // For preventing reflection hacks,
            // fields are not neither defined as member and nor cached

            LicenseKeyInfo licenseKeyInfo = LicenseKeyHelper.decodeLicenseKeyInfo(PropertyUtils.getLicenseKey());
            LicenseKeyHelper.checkLicenseKeyInfo(licenseKeyInfo);
            Integer featureLevel = licenseKeyInfo.getProperty("featureLevel");
            if (featureLevel != null) {
                enabled = featureLevel >= level;
            }
        } catch (Throwable t) {
            LOGGER.error(
                    String.format(
                            "Error occurred while checking feature '%s' " +
                            "whether or not it is enabled at level %d",
                            featureName, level),
                    t);
        }
        if (!enabled) {
            LOGGER.info(String.format(
                    "Feature '%s' has been checked whether or not it is enabled at level %d, but it is not",
                    featureName, level));
        }
        return enabled;
    }

}
