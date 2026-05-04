package com.routeplanner.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeplanner.backend.algorithm.RouteOptimizer;
import com.routeplanner.backend.dto.StopDto;
import com.routeplanner.backend.entity.RoutePlanEntity;
import com.routeplanner.backend.entity.RouteStopEntity;
import com.routeplanner.backend.exception.OptimizationException;
import com.routeplanner.backend.service.RouteOptimizationEngineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class GoogleRouteOptimizationEngineService implements RouteOptimizationEngineService {

    private static final int GOOGLE_API_MAX_WAYPOINTS = 25;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RouteOptimizer localOptimizer;

    @Value("${google.maps.api-key:}")
    private String apiKey;

    public GoogleRouteOptimizationEngineService(ObjectMapper objectMapper, RouteOptimizer localOptimizer) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
        this.localOptimizer = localOptimizer;
    }

    @Override
    public List<RouteStopEntity> optimize(RoutePlanEntity routePlan, List<RouteStopEntity> stops) {
        validateRoutePlan(routePlan);

        if (stops == null || stops.isEmpty()) {
            return List.of();
        }

        List<RouteStopEntity> geocodedStops = filterGeocodedStops(stops);

        if (geocodedStops.size() <= 1) {
            return geocodedStops;
        }

        // 25+ durak varsa veya API key yoksa → local algoritma
        if (geocodedStops.size() > GOOGLE_API_MAX_WAYPOINTS || apiKey == null || apiKey.isBlank()) {
            log.info("Using local optimizer: {} stops (Google API limit: {})",
                    geocodedStops.size(), GOOGLE_API_MAX_WAYPOINTS);
            return optimizeLocally(routePlan, geocodedStops);
        }

        // 25 ve altı → Google Routes API dene, başarısız olursa local'e fallback
        try {
            return optimizeWithGoogleApi(routePlan, geocodedStops);
        } catch (Exception e) {
            log.warn("Google API failed, falling back to local optimizer: {}", e.getMessage());
            return optimizeLocally(routePlan, geocodedStops);
        }
    }

    private void validateRoutePlan(RoutePlanEntity routePlan) {
        if (routePlan.getStartLatitude() == null || routePlan.getStartLongitude() == null) {
            throw new OptimizationException("Başlangıç koordinatı eksik");
        }
        if (routePlan.getEndLatitude() == null || routePlan.getEndLongitude() == null) {
            throw new OptimizationException("Bitiş koordinatı eksik");
        }
    }

    private List<RouteStopEntity> filterGeocodedStops(List<RouteStopEntity> stops) {
        return stops.stream()
                .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
                .sorted(Comparator.comparing(RouteStopEntity::getSequenceNo))
                .toList();
    }

    // ------------------------------------------------------------------ //
    //  LOCAL OPTIMIZER (Nearest Neighbor + 2-Opt)
    // ------------------------------------------------------------------ //

    private List<RouteStopEntity> optimizeLocally(RoutePlanEntity routePlan, List<RouteStopEntity> stops) {
        StopDto depot = toStopDto("Başlangıç", routePlan.getStartLatitude(), routePlan.getStartLongitude());
        List<StopDto> stopDtos = stops.stream()
                .map(s -> toStopDto(s.getRawAddress(), s.getLatitude(), s.getLongitude()))
                .toList();

        RouteOptimizer.OptimizationResult result = localOptimizer.optimize(depot, stopDtos);

        List<RouteStopEntity> optimized = new ArrayList<>();
        for (int i = 0; i < result.orderedStops().size(); i++) {
            StopDto dto = result.orderedStops().get(i);
            RouteStopEntity entity = findByCoordinates(stops, dto.getLatitude(), dto.getLongitude());

            entity.setPreviousSequenceNo(entity.getSequenceNo());
            entity.setSequenceNo(i + 1);
            entity.setOptimizationRound(
                    entity.getOptimizationRound() == null ? 1 : entity.getOptimizationRound() + 1
            );
            optimized.add(entity);
        }

        routePlan.setEstimatedTotalDistanceMeters((long) (result.totalDistanceKm() * 1000));
        routePlan.setEstimatedTotalDurationSeconds((long) (result.totalDistanceKm() / 40.0 * 3600));

        return optimized;
    }

    private StopDto toStopDto(String label, BigDecimal lat, BigDecimal lng) {
        return StopDto.builder()
                .label(label)
                .latitude(lat.doubleValue())
                .longitude(lng.doubleValue())
                .build();
    }

    private RouteStopEntity findByCoordinates(List<RouteStopEntity> stops, double lat, double lng) {
        return stops.stream()
                .filter(s -> Math.abs(s.getLatitude().doubleValue() - lat) < 1e-6
                        && Math.abs(s.getLongitude().doubleValue() - lng) < 1e-6)
                .findFirst()
                .orElseThrow(() -> new OptimizationException("Stop not found in original list"));
    }

    // ------------------------------------------------------------------ //
    //  GOOGLE ROUTES API
    // ------------------------------------------------------------------ //

    private List<RouteStopEntity> optimizeWithGoogleApi(RoutePlanEntity routePlan, List<RouteStopEntity> stops) {
        try {
            String requestBody = buildRequestBody(routePlan, stops);

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
                throw new OptimizationException("Google Routes API başarısız: " + response.getStatusCode());
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode routes = root.path("routes");
            if (!routes.isArray() || routes.isEmpty()) {
                throw new OptimizationException("Optimize edilmiş rota bulunamadı");
            }

            JsonNode route = routes.get(0);
            JsonNode optimizedIndexes = route.path("optimizedIntermediateWaypointIndex");

            List<RouteStopEntity> optimized = new ArrayList<>();
            for (JsonNode indexNode : optimizedIndexes) {
                int idx = indexNode.asInt();
                optimized.add(stops.get(idx));
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
            throw new OptimizationException("Google route optimization hatası", e);
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