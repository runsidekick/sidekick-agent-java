package com.runsidekick.agent.broker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author serkan
 */
public class BrokerCredentials {

    private String apiKey;
    private String appInstanceId;
    private String appName;
    private String appStage;
    private String appVersion;
    private String hostName;
    private String runtime;
    private Map<String, String> appTags;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAppInstanceId() {
        return appInstanceId;
    }

    public void setAppInstanceId(String appInstanceId) {
        this.appInstanceId = appInstanceId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppStage() {
        return appStage;
    }

    public void setAppStage(String appStage) {
        this.appStage = appStage;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public Map<String, String> getAppTags() {
        return appTags;
    }

    public void setAppTags(Map<String, String> appTags) {
        this.appTags = appTags;
    }

    public void addAppTag(String appTagName, String appTagValue) {
        if (appTags == null) {
            appTags = new HashMap<>();
        }
        appTags.put(appTagName, appTagValue);
    }

}
