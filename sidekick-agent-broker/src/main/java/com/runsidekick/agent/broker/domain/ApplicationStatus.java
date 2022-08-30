package com.runsidekick.agent.broker.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.*;

/**
 * @author serkan
 */
public class ApplicationStatus {

    private String instanceId;
    private String name;
    private String stage;
    private String version;
    private String ip;
    private String hostName;
    private String runtime;
    private Map<String, String> tags;
    private Map<String, Object> attributes;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public void addTag(String tagName, String tagValue) {
        if (tags == null) {
            tags = new HashMap<>();
        }
        tags.put(tagName, tagValue);
    }

    public void addAttribute(String name, Object value) {
        if (value == null) {
            return;
        }
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, value);
    }

    @JsonValue
    public Map<String, Object> toValues() {
        int currentSize = 6 + (attributes != null ? attributes.size() : 0);
        Map<String, Object> values = new HashMap(2 * currentSize);
        if (instanceId != null) {
            values.put("instanceId", instanceId);
        }
        if (name != null) {
            values.put("name", name);
        }
        if (stage != null) {
            values.put("stage", stage);
        }
        if (version != null) {
            values.put("version", version);
        }
        if (ip != null) {
            values.put("ip", ip);
        }
        if (hostName != null) {
            values.put("hostName", hostName);
        }
        if (runtime != null) {
            values.put("runtime", runtime);
        }
        if (tags != null) {
            values.put("tags", tags);
        }
        if (attributes != null) {
            values.putAll(attributes);
        }
        return values;
    }

    @Override
    public String toString() {
        return "Application{" +
                "instanceId='" + instanceId + '\'' +
                ", name='" + name + '\'' +
                ", stage='" + stage + '\'' +
                ", version='" + version + '\'' +
                ", ip='" + ip + '\'' +
                ", hostName='" + hostName + '\'' +
                ", runtime='" + runtime + '\'' +
                ", tags=" + tags +
                ", attributes=" + attributes +
                '}';
    }

}
