package com.routeplanner.backend.Service;

import com.routeplanner.backend.DTO.Request.PushMessageRequest;

public interface PushNotificationSenderService {
    String send(PushMessageRequest request);
}
