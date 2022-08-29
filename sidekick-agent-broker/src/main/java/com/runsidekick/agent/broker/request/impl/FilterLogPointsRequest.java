package com.runsidekick.agent.broker.request.impl;

import java.util.Map;

/**
 * @author yasin
 */
public class FilterLogPointsRequest extends BaseRequest {

    private ApplicationFilter applicationFilter = new ApplicationFilter();

    public FilterLogPointsRequest() {

    }

    public FilterLogPointsRequest(String name, String version, String stage, Map<String, Object> customTags) {
        ApplicationFilter applicationFilter = new ApplicationFilter();
        applicationFilter.setName(name);
        applicationFilter.setVersion(version);
        applicationFilter.setStage(stage);
        applicationFilter.setCustomTags(customTags);

        this.applicationFilter = applicationFilter;
    }

    public ApplicationFilter getApplicationFilter() {
        return applicationFilter;
    }

    public void setApplicationFilter(ApplicationFilter applicationFilter) {
        this.applicationFilter = applicationFilter;
    }

    @Override
    public String toString() {
        return "FilterLogPointsRequest{" +
                "ApplicationFilter{" +
                "name='" + applicationFilter.getName() + '\'' +
                ", stage='" + applicationFilter.getStage() + '\'' +
                ", version='" + applicationFilter.getVersion() + '\'' +
                ", customTags='" + applicationFilter.getCustomTags() + '\'' +
                '}' + '\'' +
                '}';
    }

    class ApplicationFilter {

        private String name;
        private String version;
        private String stage;
        private Map<String, Object> customTags;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getStage() {
            return stage;
        }

        public void setStage(String stage) {
            this.stage = stage;
        }

        public Map<String, Object> getCustomTags() {
            return customTags;
        }

        public void setCustomTags(Map<String, Object> customTags) {
            this.customTags = customTags;
        }

        @Override
        public String toString() {
            return "ApplicationFilter{" +
                    "name='" + name + '\'' +
                    ", stage='" + stage + '\'' +
                    ", version='" + version + '\'' +
                    ", customTags='" + customTags + '\'' +
                    '}';
        }

    }
}
