package com.routeplanner.backend.Controller;

import com.routeplanner.backend.Enums.NotificationEventTypeEnum;
import com.routeplanner.backend.Service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationTestController {

    private final NotificationService notificationService;

    public NotificationTestController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/test/{userId}")
    public Map<String, Object> sendTest(@PathVariable UUID userId) {
        notificationService.sendToUser(
                userId,
                NotificationEventTypeEnum.ROUTE_REOPTIMIZED,
                "Test bildirimi",
                "Bildirim sistemi çalışıyor.",
                Map.of("event", "TEST_NOTIFICATION")
        );

        return Map.of(
                "success", true,
                "message", "Test bildirimi kuyruğa alındı"
        );
    }
}
