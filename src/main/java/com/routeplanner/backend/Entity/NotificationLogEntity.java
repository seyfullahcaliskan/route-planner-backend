package com.routeplanner.backend.Entity;

import com.routeplanner.backend.Enums.NotificationDeliveryStatusEnum;
import com.routeplanner.backend.Enums.NotificationEventTypeEnum;
import com.routeplanner.backend.Enums.NotificationProviderEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "notification_log")
public class NotificationLogEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_token_id")
    private DeviceTokenEntity deviceToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private NotificationEventTypeEnum eventType;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "body", nullable = false, length = 500)
    private String body;

    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 30)
    private NotificationProviderEnum provider = NotificationProviderEnum.EXPO;

    @Column(name = "provider_ticket_id", length = 255)
    private String providerTicketId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 30)
    private NotificationDeliveryStatusEnum deliveryStatus = NotificationDeliveryStatusEnum.PENDING;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public DeviceTokenEntity getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(DeviceTokenEntity deviceToken) {
        this.deviceToken = deviceToken;
    }

    public NotificationEventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(NotificationEventTypeEnum eventType) {
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public NotificationProviderEnum getProvider() {
        return provider;
    }

    public void setProvider(NotificationProviderEnum provider) {
        this.provider = provider;
    }

    public String getProviderTicketId() {
        return providerTicketId;
    }

    public void setProviderTicketId(String providerTicketId) {
        this.providerTicketId = providerTicketId;
    }

    public NotificationDeliveryStatusEnum getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(NotificationDeliveryStatusEnum deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
