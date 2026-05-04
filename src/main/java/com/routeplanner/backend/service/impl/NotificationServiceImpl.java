package com.routeplanner.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeplanner.backend.dto.request.PushMessageRequest;
import com.routeplanner.backend.entity.DeviceTokenEntity;
import com.routeplanner.backend.entity.NotificationLogEntity;
import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.enums.NotificationDeliveryStatusEnum;
import com.routeplanner.backend.enums.NotificationEventTypeEnum;
import com.routeplanner.backend.enums.NotificationProviderEnum;
import com.routeplanner.backend.repository.DeviceTokenRepository;
import com.routeplanner.backend.repository.NotificationLogRepository;
import com.routeplanner.backend.repository.UserRepository;
import com.routeplanner.backend.service.NotificationService;
import com.routeplanner.backend.service.PushNotificationSenderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final UserRepository userRepository;
    private final PushNotificationSenderService pushNotificationSenderService;
    private final ObjectMapper objectMapper;

    public NotificationServiceImpl(DeviceTokenRepository deviceTokenRepository,
                                   NotificationLogRepository notificationLogRepository,
                                   UserRepository userRepository,
                                   PushNotificationSenderService pushNotificationSenderService,
                                   ObjectMapper objectMapper) {
        this.deviceTokenRepository = deviceTokenRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.userRepository = userRepository;
        this.pushNotificationSenderService = pushNotificationSenderService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendToUser(UUID userId,
                           NotificationEventTypeEnum eventType,
                           String title,
                           String body,
                           Map<String, Object> data) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<DeviceTokenEntity> activeTokens = deviceTokenRepository.findByUserIdAndIsActiveTrue(userId);

        for (DeviceTokenEntity token : activeTokens) {
            NotificationLogEntity log = new NotificationLogEntity();
            log.setUser(user);
            log.setDeviceToken(token);
            log.setEventType(eventType);
            log.setTitle(title);
            log.setBody(body);
            log.setProvider(NotificationProviderEnum.EXPO);
            log.setDeliveryStatus(NotificationDeliveryStatusEnum.PENDING);

            try {
                if (data != null) {
                    log.setPayload(objectMapper.writeValueAsString(data));
                }

                PushMessageRequest request = new PushMessageRequest();
                request.setTo(token.getExpoPushToken());
                request.setTitle(title);
                request.setBody(body);
                request.setData(data);

                String ticketId = pushNotificationSenderService.send(request);

                log.setProviderTicketId(ticketId);
                log.setDeliveryStatus(NotificationDeliveryStatusEnum.SENT);
            } catch (Exception e) {
                log.setDeliveryStatus(NotificationDeliveryStatusEnum.ERROR);
                log.setErrorMessage(e.getMessage());
            }

            notificationLogRepository.save(log);
        }
    }
}
