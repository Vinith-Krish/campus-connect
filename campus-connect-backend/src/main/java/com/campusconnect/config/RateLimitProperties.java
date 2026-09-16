package com.campusconnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private int authIpCapacity = 20;
    private int authAccountCapacity = 8;
    private int publicCapacity = 120;
    private int authenticatedCapacity = 300;
    private long authIpWindowSeconds = 60;
    private long authAccountWindowSeconds = 300;
    private long publicWindowSeconds = 60;
    private long authenticatedWindowSeconds = 60;
    private long backoffBaseSeconds = 2;
    private long backoffMaxSeconds = 300;
    private int maxTrackedKeys = 100000;

    public int getAuthIpCapacity() {
        return authIpCapacity;
    }

    public void setAuthIpCapacity(int authIpCapacity) {
        this.authIpCapacity = authIpCapacity;
    }

    public int getAuthAccountCapacity() {
        return authAccountCapacity;
    }

    public void setAuthAccountCapacity(int authAccountCapacity) {
        this.authAccountCapacity = authAccountCapacity;
    }

    public int getPublicCapacity() {
        return publicCapacity;
    }

    public void setPublicCapacity(int publicCapacity) {
        this.publicCapacity = publicCapacity;
    }

    public int getAuthenticatedCapacity() {
        return authenticatedCapacity;
    }

    public void setAuthenticatedCapacity(int authenticatedCapacity) {
        this.authenticatedCapacity = authenticatedCapacity;
    }

    public long getAuthIpWindowSeconds() {
        return authIpWindowSeconds;
    }

    public void setAuthIpWindowSeconds(long authIpWindowSeconds) {
        this.authIpWindowSeconds = authIpWindowSeconds;
    }

    public long getAuthAccountWindowSeconds() {
        return authAccountWindowSeconds;
    }

    public void setAuthAccountWindowSeconds(long authAccountWindowSeconds) {
        this.authAccountWindowSeconds = authAccountWindowSeconds;
    }

    public long getPublicWindowSeconds() {
        return publicWindowSeconds;
    }

    public void setPublicWindowSeconds(long publicWindowSeconds) {
        this.publicWindowSeconds = publicWindowSeconds;
    }

    public long getAuthenticatedWindowSeconds() {
        return authenticatedWindowSeconds;
    }

    public void setAuthenticatedWindowSeconds(long authenticatedWindowSeconds) {
        this.authenticatedWindowSeconds = authenticatedWindowSeconds;
    }

    public long getBackoffBaseSeconds() {
        return backoffBaseSeconds;
    }

    public void setBackoffBaseSeconds(long backoffBaseSeconds) {
        this.backoffBaseSeconds = backoffBaseSeconds;
    }

    public long getBackoffMaxSeconds() {
        return backoffMaxSeconds;
    }

    public void setBackoffMaxSeconds(long backoffMaxSeconds) {
        this.backoffMaxSeconds = backoffMaxSeconds;
    }

    public int getMaxTrackedKeys() {
        return maxTrackedKeys;
    }

    public void setMaxTrackedKeys(int maxTrackedKeys) {
        this.maxTrackedKeys = maxTrackedKeys;
    }
}
