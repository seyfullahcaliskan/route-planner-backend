package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.request.PushMessageRequest;

public interface PushNotificationSenderService {
    String send(PushMessageRequest request);
}
