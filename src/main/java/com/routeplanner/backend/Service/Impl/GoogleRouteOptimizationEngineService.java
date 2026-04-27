package com.routeplanner.backend.Service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeplanner.backend.Entity.RoutePlanEntity;
import com.routeplanner.backend.Entity.RouteStopEntity;
import com.routeplanner.backend.Service.RouteOptimizationEngineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GoogleRouteOptimizationEngineService implements RouteOptimizationEngineService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.maps.api-key}")
    private String apiKey;

    public GoogleRouteOptimizationEngineService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public List<RouteStopEntity> optimize(RoutePlanEntity routePlan, List<RouteStopEntity> stops) {
        if (routePlan.getStartLatitude() == null || routePlan.getStartLongitude() == null) {
            throw new RuntimeException("Başlangıç koordinatı eksik. Route oluşturulurken başlangıç adresi geocode edilmemiş.");
        }

        if (routePlan.getEndLatitude() == null || routePlan.getEndLongitude() == null) {
            throw new RuntimeException("Bitiş koordinatı eksik. Route oluşturulurken bitiş adresi geocode edilmemiş.");
        }

        if (stops == null || stops.isEmpty()) {
            return List.of();
        }

        if (stops.size() > 25) {
            throw new RuntimeException("Tek optimizasyon isteğinde maksimum 25 durak destekleniyor.");
        }

        try {
            List<RouteStopEntity> geocodedStops = stops.stream()
                    .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
                    .sorted(Comparator.comparing(RouteStopEntity::getSequenceNo))
                    .toList();

            if (geocodedStops.size() <= 1) {
                return geocodedStops;
            }

            String requestBody = buildRequestBody(routePlan, geocodedStops);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Goog-Api-Key", apiKey);
            headers.set("X-Goog-FieldMask",
                    "routes.optimizedIntermediateWaypointIndex,routes.distanceMeters,routes.duration");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://routes.googleapis.com/directions/v2:computeRoutes",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Routes API başarısız");
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode routes = root.path("routes");
            if (!routes.isArray() || routes.isEmpty()) {
                throw new RuntimeException("Optimize edilmiş rota bulunamadı");
            }

            JsonNode route = routes.get(0);
            JsonNode optimizedIndexes = route.path("optimizedIntermediateWaypointIndex");

            List<RouteStopEntity> optimized = new ArrayList<>();
            for (JsonNode indexNode : optimizedIndexes) {
                int idx = indexNode.asInt();
                optimized.add(geocodedStops.get(idx));
            }

            for (int i = 0; i < optimized.size(); i++) {
                optimized.get(i).setPreviousSequenceNo(optimized.get(i).getSequenceNo());
                optimized.get(i).setSequenceNo(i + 1);
                optimized.get(i).setOptimizationRound(
                        optimized.get(i).getOptimizationRound() == null
                                ? 1
                                : optimized.get(i).getOptimizationRound() + 1
                );
            }

            routePlan.setEstimatedTotalDistanceMeters(route.path("distanceMeters").asLong());
            routePlan.setEstimatedTotalDurationSeconds(parseDurationSeconds(route.path("duration").asText()));

            return optimized;
        } catch (Exception e) {
            throw new RuntimeException("Google route optimization hatası", e);
        }
    }

    private String buildRequestBody(RoutePlanEntity routePlan, List<RouteStopEntity> stops) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"origin\":").append(buildWaypoint(
                routePlan.getStartLatitude().doubleValue(),
                routePlan.getStartLongitude().doubleValue()
        )).append(",");
        sb.append("\"destination\":").append(buildWaypoint(
                routePlan.getEndLatitude().doubleValue(),
                routePlan.getEndLongitude().doubleValue()
        )).append(",");
        sb.append("\"intermediates\":[");
        for (int i = 0; i < stops.size(); i++) {
            RouteStopEntity stop = stops.get(i);
            sb.append(buildWaypoint(stop.getLatitude().doubleValue(), stop.getLongitude().doubleValue()));
            if (i < stops.size() - 1) sb.append(",");
        }
        sb.append("],");
        sb.append("\"travelMode\":\"DRIVE\",");
        sb.append("\"optimizeWaypointOrder\":true,");
        sb.append("\"routingPreference\":\"TRAFFIC_AWARE\"");
        sb.append("}");
        return sb.toString();
    }

    private String buildWaypoint(double lat, double lng) {
        return "{"
                + "\"location\":{"
                + "\"latLng\":{"
                + "\"latitude\":" + lat + ","
                + "\"longitude\":" + lng
                + "}"
                + "}"
                + "}";
    }

    private long parseDurationSeconds(String durationText) {
        if (durationText == null || durationText.isBlank()) return 0L;
        return Long.parseLong(durationText.replace("s", ""));
    }
}
