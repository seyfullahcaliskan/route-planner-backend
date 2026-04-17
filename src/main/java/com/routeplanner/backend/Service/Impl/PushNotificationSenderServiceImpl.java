package com.routeplanner.backend.Service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeplanner.backend.DTO.Request.PushMessageRequest;
import com.routeplanner.backend.Service.PushNotificationSenderService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PushNotificationSenderServiceImpl implements PushNotificationSenderService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PushNotificationSenderServiceImpl(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public String send(PushMessageRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<PushMessageRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    EXPO_PUSH_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Expo push gönderimi başarısız");
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataNode = root.path("data");

            if (dataNode.isArray() && dataNode.size() > 0) {
                JsonNode first = dataNode.get(0);
                return first.path("id").asText(null);
            }

            if (dataNode.isObject()) {
                return dataNode.path("id").asText(null);
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException("Expo push gönderimi sırasında hata oluştu", e);
        }
    }
}
