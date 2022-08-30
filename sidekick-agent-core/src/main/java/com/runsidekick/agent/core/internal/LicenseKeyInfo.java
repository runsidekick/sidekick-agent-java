package com.runsidekick.agent.core.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent license key.
 *
 * @author serkan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LicenseKeyInfo {

    private String id;
    private String customerId;
    private long expireTime;
    private Map<String, Object> properties = new HashMap<String, Object>();

    public LicenseKeyInfo() {
    }

    public LicenseKeyInfo(String id, String customerId, long expireTime) {
        this.id = id;
        this.customerId = customerId;
        this.expireTime = expireTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public boolean hasProperty(String propName) {
        return properties.containsKey(propName);
    }

    public <V> V getProperty(String propName) {
        return (V) properties.get(propName);
    }

    public <V> V putProperty(String propName, Object propValue) {
        return (V) properties.put(propName, propValue);
    }

    public <V> V putPropertyIfAbsent(String propName, Object propValue) {
        return (V) properties.putIfAbsent(propName, propValue);
    }

    public <V> V removeProperty(String propName) {
        return (V) properties.remove(propName);
    }

    @Override
    public String toString() {
        return "LicenseKeyInfo{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", expireTime=" + expireTime +
                ", properties=" + properties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LicenseKeyInfo that = (LicenseKeyInfo) o;

        if (expireTime != that.expireTime) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null) return false;
        return properties != null ? properties.equals(that.properties) : that.properties == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
        result = 31 * result + (int) (expireTime ^ (expireTime >>> 32));
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

}
