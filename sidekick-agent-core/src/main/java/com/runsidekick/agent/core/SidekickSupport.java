package com.runsidekick.agent.core;

import com.runsidekick.agent.core.app.Application;
import com.runsidekick.agent.core.app.ApplicationInfo;
import com.runsidekick.agent.core.app.ApplicationInfoProvider;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.ClassUtils;
import com.runsidekick.agent.core.util.PropertyUtils;
import com.runsidekick.agent.core.util.StringUtils;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * Mediator class for global Sidekick related operations.
 *
 * @author serkan
 */
public final class SidekickSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SidekickSupport.class);

    private static final boolean DEBUG_ENABLE =
            PropertyUtils.getBooleanProperty("sidekick.agent.debug.enable", false);
    public static final String DEFAULT_APPLICATION_ID_PREFIX;
    public static final String APPLICATION_PLATFORM;
    public static final String APPLICATION_REGION;

    static {
        String applicationPlatform =
                PropertyUtils.getStringProperty("sidekick.agent.application.platform", null);
        if (StringUtils.isNullOrEmpty(applicationPlatform)) {
            applicationPlatform = "JVM";
            if (StringUtils.hasValue(System.getenv("ECS_CONTAINER_METADATA_URI"))) {
                applicationPlatform = "AWS ECS";
            } else if (StringUtils.hasValue(System.getenv("KUBERNETES_SERVICE_HOST"))) {
                applicationPlatform = "K8S";
            }
        }
        APPLICATION_PLATFORM = applicationPlatform;

        String applicationRegion = PropertyUtils.getStringProperty("sidekick.agent.application.region", "");
        if (StringUtils.isNullOrEmpty(applicationRegion)
                && PropertyUtils.getBooleanProperty("sidekick.agent.application.region.discovery.enable", false)) {
            try {
                // If application region is not specified through configuration,
                // try to get it from EC2 metadata service
                Class ec2MetadataClass = ClassUtils.getClassWithException("com.amazonaws.util.EC2MetadataUtils");
                Method getEC2InstanceRegionMethod = ec2MetadataClass.getMethod("getEC2InstanceRegion");
                applicationRegion = (String) getEC2InstanceRegionMethod.invoke(null);
            } catch (Throwable t) {
            }
        }
        APPLICATION_REGION = applicationRegion;

        String applicationType =
                StringUtils.toLowerCase(applicationPlatform).replaceAll("\\s+", "-");
        DEFAULT_APPLICATION_ID_PREFIX =
                "java" + ":" + applicationType + ":" + applicationRegion + ":";
    }

    private SidekickSupport() {
    }

    //////////////////////////////////////////////////////////////////////////////////

    public static boolean isDebugEnable() {
        return DEBUG_ENABLE;
    }

    //////////////////////////////////////////////////////////////////////////////////

    public static ApplicationInfoProvider getApplicationInfoProvider() {
        return Application.getApplicationInfoProvider();
    }

    public static void setApplicationInfoProvider(ApplicationInfoProvider applicationInfoProvider) {
        Application.setApplicationInfoProvider(applicationInfoProvider);
    }

    public static ApplicationInfo getApplicationInfo() {
        return Application.getApplicationInfo();
    }

    public static String getApplicationRegion() {
        return APPLICATION_REGION;
    }

}
