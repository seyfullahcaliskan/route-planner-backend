package com.routeplanner.backend.service;

import com.routeplanner.backend.enums.NotificationEventTypeEnum;

import java.util.Map;
import java.util.UUID;

public interface NotificationService {
    void sendToUser(UUID userId,
                    NotificationEventTypeEnum eventType,
                    String title,
                    String body,
                    Map<String, Object> data);
}
