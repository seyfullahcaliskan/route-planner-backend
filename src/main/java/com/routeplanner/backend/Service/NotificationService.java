package com.routeplanner.backend.Service;

import com.routeplanner.backend.Enums.NotificationEventTypeEnum;

import java.util.Map;
import java.util.UUID;

public interface NotificationService {
    void sendToUser(UUID userId,
                    NotificationEventTypeEnum eventType,
                    String title,
                    String body,
                    Map<String, Object> data);
}
