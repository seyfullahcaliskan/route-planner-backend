package com.routeplanner.backend.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "device_token")
public class DeviceTokenEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "expo_push_token", nullable = false, unique = true, length = 255)
    private String expoPushToken;

    @Column(name = "platform", nullable = false, length = 20)
    private String platform;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "app_version", length = 50)
    private String appVersion;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getExpoPushToken() {
        return expoPushToken;
    }

    public void setExpoPushToken(String expoPushToken) {
        this.expoPushToken = expoPushToken;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}