package com.runsidekick.agent.core.app;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents application specific information
 * such as application name, id, version, etc ...
 *
 * @author serkan
 */
public class ApplicationInfo {

    private String applicationId;
    private String applicationInstanceId;
    private String applicationDomainName;
    private String applicationClassName;
    private String applicationName;
    private String applicationVersion;
    private String applicationStage;
    private String applicationRuntime;
    private String applicationRuntimeVersion;
    private Map<String, Object> applicationTags;

    public ApplicationInfo() {
    }

    public ApplicationInfo(String applicationId, String applicationInstanceId, String applicationDomainName,
                           String applicationClassName, String applicationName,
                           String applicationVersion, String applicationStage,
                           String applicationRuntime, String applicationRuntimeVersion,
                           Map<String, Object> applicationTags) {
        this.applicationId = applicationId;
        this.applicationInstanceId = applicationInstanceId;
        this.applicationDomainName = applicationDomainName;
        this.applicationClassName = applicationClassName;
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.applicationStage = applicationStage;
        this.applicationRuntime = applicationRuntime;
        this.applicationRuntimeVersion = applicationRuntimeVersion;
        this.applicationTags = applicationTags;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationInstanceId() {
        return applicationInstanceId;
    }

    public void setApplicationInstanceId(String applicationInstanceId) {
        this.applicationInstanceId = applicationInstanceId;
    }

    public String getApplicationDomainName() {
        return applicationDomainName;
    }

    public void setApplicationDomainName(String applicationDomainName) {
        this.applicationDomainName = applicationDomainName;
    }

    public String getApplicationClassName() {
        return applicationClassName;
    }

    public void setApplicationClassName(String applicationClassName) {
        this.applicationClassName = applicationClassName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public String getApplicationStage() {
        return applicationStage;
    }

    public void setApplicationStage(String applicationStage) {
        this.applicationStage = applicationStage;
    }

    public String getApplicationRuntime() {
        return applicationRuntime;
    }

    public void setApplicationRuntime(String applicationRuntime) {
        this.applicationRuntime = applicationRuntime;
    }

    public String getApplicationRuntimeVersion() {
        return applicationRuntimeVersion;
    }

    public void setApplicationRuntimeVersion(String applicationRuntimeVersion) {
        this.applicationRuntimeVersion = applicationRuntimeVersion;
    }

    public Map<String, Object> getApplicationTags() {
        if (applicationTags == null) {
            return null;
        }
        return Collections.unmodifiableMap(applicationTags);
    }

    public void setApplicationTags(Map<String, Object> applicationTags) {
        if (applicationTags != null) {
            applicationTags.values().stream().forEach((v) -> {
                if (!(v instanceof String) &&
                        !(v instanceof Number) &&
                        !(v instanceof Boolean)) {
                    throw new IllegalArgumentException(
                            "Only string, numeric or boolean typed values can be used");
                }
            });
        }
        this.applicationTags = applicationTags;
    }

    public <T> T getApplicationTag(String tagName) {
        if (applicationTags == null) {
            return null;
        } else {
            return (T) applicationTags.get(tagName);
        }
    }

    public Boolean getBooleanApplicationTag(String tagName) {
        Object tagValue = getApplicationTag(tagName);
        if (tagValue == null) {
            return null;
        } else {
            if (tagValue instanceof Boolean) {
                return ((Boolean) tagValue);
            } else {
                throw new IllegalArgumentException(
                        String.format("Tag value associated with given tag name '%s' is not boolean", tagName));
            }
        }
    }

    private Number getNumericApplicationTag(String tagName) {
        Object tagValue = applicationTags.get(tagName);
        if (tagValue == null) {
            return null;
        } else {
            if (tagValue instanceof Number) {
                return ((Number) tagValue);
            } else {
                throw new IllegalArgumentException(
                        String.format("Tag value associated with given tag name '%s' is not numeric", tagName));
            }
        }
    }

    public Byte getByteApplicationTag(String tagName) {
        Number numberVal = getNumericApplicationTag(tagName);
        if (numberVal == null) {
            return null;
        } else {
            return numberVal.byteValue();
        }
    }

    public Short getShortApplicationTag(String tagName) {
        Number numberVal = getNumericApplicationTag(tagName);
        if (numberVal == null) {
            return null;
        } else {
            return numberVal.shortValue();
        }
    }

    public Integer getIntegerApplicationTag(String tagName) {
        Number numberVal = getNumericApplicationTag(tagName);
        if (numberVal == null) {
            return null;
        } else {
            return numberVal.intValue();
        }
    }

    public Float getFloatApplicationTag(String tagName) {
        Number numberVal = getNumericApplicationTag(tagName);
        if (numberVal == null) {
            return null;
        } else {
            return numberVal.floatValue();
        }
    }

    public Long getLongApplicationTag(String tagName) {
        Number numberVal = getNumericApplicationTag(tagName);
        if (numberVal == null) {
            return null;
        } else {
            return numberVal.longValue();
        }
    }

    public Double getDoubleApplicationTag(String tagName) {
        Number numberVal = getNumericApplicationTag(tagName);
        if (numberVal == null) {
            return null;
        } else {
            return numberVal.doubleValue();
        }
    }

    public String getStringApplicationTag(String tagName) {
        Object tagValue = getApplicationTag(tagName);
        if (tagValue == null) {
            return null;
        } else {
            if (tagValue instanceof String) {
                return ((String) tagValue);
            } else {
                throw new IllegalArgumentException(
                        String.format("Tag value associated with given tag name '%s' is not string", tagName));
            }
        }
    }

    private void doSetApplicationTag(String tagName, Object tagValue) {
        if (applicationTags == null) {
            applicationTags = new HashMap<String, Object>();
        }
        applicationTags.put(tagName, tagValue);
    }

    public void setApplicationTag(String tagName, String tagValue) {
        doSetApplicationTag(tagName, tagValue);
    }

    public void setApplicationTag(String tagName, Number tagValue) {
        doSetApplicationTag(tagName, tagValue);
    }

    public void setApplicationTag(String tagName, Boolean tagValue) {
        doSetApplicationTag(tagName, tagValue);
    }

    @Override
    public String toString() {
        return "ApplicationInfo{" +
                "applicationId='" + applicationId + '\'' +
                ", applicationInstanceId='" + applicationInstanceId + '\'' +
                ", applicationDomainName='" + applicationDomainName + '\'' +
                ", applicationClassName='" + applicationClassName + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", applicationVersion='" + applicationVersion + '\'' +
                ", applicationStage='" + applicationStage + '\'' +
                ", applicationRuntime='" + applicationRuntime + '\'' +
                ", applicationRuntimeVersion='" + applicationRuntimeVersion + '\'' +
                ", applicationTags=" + applicationTags +
                '}';
    }

}
