package com.runsidekick.agent.core.app.impl;

import com.runsidekick.agent.core.SidekickSupport;
import com.runsidekick.agent.core.app.ApplicationInfo;
import com.runsidekick.agent.core.app.ApplicationInfoProvider;
import com.runsidekick.agent.core.property.PropertyAccessor;
import com.runsidekick.agent.core.property.StandardPropertyAccessor;
import com.runsidekick.agent.core.util.PropertyUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * {@link ApplicationInfoProvider} implementation
 * which provides {@link ApplicationInfo application information}
 * from application properties through given {@link PropertyAccessor} or
 * {@link PropertyUtils#PROPERTY_ACCESSOR} by default
 * (via {@link StandardPropertyAccessor#DEFAULT} under the hood).
 *
 * @author serkan
 */
public class PropertyAwareApplicationInfoProvider implements ApplicationInfoProvider {

    public static final String APPLICATION_ID_PROP_NAME = "sidekick.agent.application.id";
    public static final String APPLICATION_INSTANCE_ID_PROP_NAME = "sidekick.agent.application.instanceid";
    public static final String APPLICATION_DOMAIN_NAME_PROP_NAME = "sidekick.agent.application.domainname";
    public static final String APPLICATION_CLASS_NAME_PROP_NAME = "sidekick.agent.application.classname";
    public static final String APPLICATION_NAME_PROP_NAME = "sidekick.agent.application.name";
    public static final String APPLICATION_VERSION_PROP_NAME = "sidekick.agent.application.version";
    public static final String APPLICATION_STAGE_PROP_NAME = "sidekick.agent.application.stage";
    public static final String APPLICATION_TAG_PROP_NAME_PREFIX = "sidekick.agent.application.tag.";

    private final String applicationDomainName;
    private final String applicationClassName;
    private final String applicationName;
    private final String applicationId;
    private final String applicationInstanceId;
    private final String applicationVersion;
    private final String applicationStage;
    private final Map<String, Object> applicationTags;

    public PropertyAwareApplicationInfoProvider() {
        this(PropertyUtils.PROPERTY_ACCESSOR);
    }

    public PropertyAwareApplicationInfoProvider(PropertyAccessor propertyAccessor) {
        this.applicationDomainName =
                propertyAccessor.getProperty(APPLICATION_DOMAIN_NAME_PROP_NAME, "");
        this.applicationClassName =
                propertyAccessor.getProperty(APPLICATION_CLASS_NAME_PROP_NAME, "");
        this.applicationName =
                propertyAccessor.getProperty(APPLICATION_NAME_PROP_NAME, "");
        this.applicationId =
                propertyAccessor.getProperty(
                        APPLICATION_ID_PROP_NAME,
                        getDefaultApplicationId(applicationName));
        this.applicationInstanceId =
                propertyAccessor.getProperty(
                        APPLICATION_INSTANCE_ID_PROP_NAME,
                        getDefaultApplicationInstanceId());
        this.applicationVersion =
                propertyAccessor.getProperty(APPLICATION_VERSION_PROP_NAME, "");
        this.applicationStage =
                propertyAccessor.getProperty(APPLICATION_STAGE_PROP_NAME, "");

        Map<String, Object> tags = null;
        Map<String, String> sysProps = propertyAccessor.getProperties();
        for (String propName : sysProps.keySet()) {
            if (propName.startsWith(APPLICATION_TAG_PROP_NAME_PREFIX)) {
                String propValue = sysProps.get(propName);
                String tagName = propName.substring(APPLICATION_TAG_PROP_NAME_PREFIX.length());
                Object tagValue;
                if (propValue.startsWith("\"") && propValue.endsWith("\"")) {
                    tagValue = propValue.substring(1, propValue.length() - 1);
                } else if (propValue.equalsIgnoreCase("true") ||
                        propValue.equalsIgnoreCase("false")) {
                    tagValue = Boolean.parseBoolean(propValue);
                } else {
                    Long longVal = null;
                    try {
                        // Try parsing to non-decimal number
                        longVal = Long.parseLong(propValue);
                    } catch (NumberFormatException e) {
                    }
                    if (longVal != null) {
                        tagValue = longVal;
                    } else {
                        Double doubleVal = null;
                        try {
                            // Try parsing to decimal number
                            doubleVal = Double.parseDouble(propValue);
                        } catch (NumberFormatException e) {
                        }
                        if (doubleVal != null) {
                            tagValue = doubleVal;
                        } else {
                            // Couldn't parse to decimal or non-decimal number,
                            // so accept the tag value as string typed value
                            tagValue = propValue;
                        }
                    }
                }
                if (tags == null) {
                    tags = new HashMap<String, Object>();
                }
                tags.put(tagName, tagValue);
            }
        }
        if (tags != null) {
            applicationTags = Collections.unmodifiableMap(tags);
        } else {
            applicationTags = Collections.EMPTY_MAP;
        }
    }

    private static String getDefaultApplicationId(String applicationName) {
        return SidekickSupport.DEFAULT_APPLICATION_ID_PREFIX + applicationName;
    }

    private static long getProcessId() {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        if (processName != null && processName.length() > 0) {
            try {
                return Long.parseLong(processName.split("@")[0]);
            } catch (Exception e) {
            }
        }
        return 0L;
    }

    private static String getDefaultApplicationInstanceId() {
        long procId = getProcessId();
        String appInstanceId = procId + ":" + UUID.randomUUID().toString();
        try {
            return appInstanceId + "@" + InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return appInstanceId;
        }
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        ApplicationInfo applicationInfo =
                new ApplicationInfo(
                        applicationId,
                        applicationInstanceId,
                        applicationDomainName,
                        applicationClassName,
                        applicationName,
                        applicationVersion,
                        applicationStage,
                        APPLICATION_RUNTIME,
                        APPLICATION_RUNTIME_VERSION,
                        applicationTags);
        return applicationInfo;
    }

}
