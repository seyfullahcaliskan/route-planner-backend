package com.routeplanner.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeplanner.backend.dto.request.ReoptimizeRouteRequest;
import com.routeplanner.backend.entity.RoutePlanEntity;
import com.routeplanner.backend.entity.RouteReoptimizationHistoryEntity;
import com.routeplanner.backend.entity.RouteStopEntity;
import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.enums.ReoptimizationReasonEnum;
import com.routeplanner.backend.enums.RoutePlanStatusEnum;
import com.routeplanner.backend.enums.RouteStopStatusEnum;
import com.routeplanner.backend.repository.RoutePlanRepository;
import com.routeplanner.backend.repository.RouteReoptimizationHistoryRepository;
import com.routeplanner.backend.repository.RouteStopRepository;
import com.routeplanner.backend.repository.UserRepository;
import com.routeplanner.backend.service.RouteOptimizationEngineService;
import com.routeplanner.backend.service.RouteOptimizationService;
import com.routeplanner.backend.enums.NotificationEventTypeEnum;
import com.routeplanner.backend.service.NotificationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Transactional
public class RouteOptimizationServiceImpl implements RouteOptimizationService {

    private final RoutePlanRepository routePlanRepository;
    private final RouteStopRepository routeStopRepository;
    private final RouteReoptimizationHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final RouteOptimizationEngineService routeOptimizationEngineService;

    public RouteOptimizationServiceImpl(RoutePlanRepository routePlanRepository,
                                        RouteStopRepository routeStopRepository,
                                        RouteReoptimizationHistoryRepository historyRepository,
                                        UserRepository userRepository,
                                        ObjectMapper objectMapper, NotificationService notificationService, RouteOptimizationEngineService routeOptimizationEngineService) {
        this.routePlanRepository = routePlanRepository;
        this.routeStopRepository = routeStopRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
        this.routeOptimizationEngineService = routeOptimizationEngineService;
    }

    @Override
    public List<RouteStopEntity> reoptimizeRoute(UUID routePlanId, UUID triggeredByUserId, ReoptimizeRouteRequest request) {
        RoutePlanEntity routePlan = routePlanRepository.findById(routePlanId)
                .orElseThrow(() -> new RuntimeException("Route plan not found: " + routePlanId));

        UserEntity triggeredByUser = null;
        if (triggeredByUserId != null) {
            triggeredByUser = userRepository.findById(triggeredByUserId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + triggeredByUserId));
        }

        List<RouteStopEntity> allStops = routeStopRepository.findByRoutePlanIdOrderBySequenceNoAsc(routePlanId);

        String previousSnapshot = toSnapshot(allStops);

        List<RouteStopEntity> completedStops = allStops.stream()
                .filter(stop -> stop.getStopStatus() == RouteStopStatusEnum.DELIVERED)
                .toList();

        List<RouteStopEntity> optimizableStops = allStops.stream()
                .filter(stop -> stop.getStopStatus() != RouteStopStatusEnum.DELIVERED)
                .toList();

        List<RouteStopEntity> optimizedStops = routeOptimizationEngineService.optimize(routePlan, optimizableStops);

        int baseSequence = completedStops.size();

        for (int i = 0; i < optimizedStops.size(); i++) {
            RouteStopEntity stop = optimizedStops.get(i);
            stop.setSequenceNo(baseSequence + i + 1);

            if (stop.getStopStatus() == RouteStopStatusEnum.SKIPPED
                    || stop.getStopStatus() == RouteStopStatusEnum.FAILED
                    || stop.getStopStatus() == RouteStopStatusEnum.POSTPONED) {
                stop.setStopStatus(RouteStopStatusEnum.PENDING);
            }

            routeStopRepository.save(stop);
        }

        routePlan.setPlanStatus(RoutePlanStatusEnum.READY);
        routePlan.setLastOptimizedAt(now());
        routePlanRepository.save(routePlan);

        List<RouteStopEntity> updatedStops = routeStopRepository.findByRoutePlanIdOrderBySequenceNoAsc(routePlanId);

        RouteReoptimizationHistoryEntity history = new RouteReoptimizationHistoryEntity();
        history.setRoutePlan(routePlan);
        history.setOptimizationRound(resolveOptimizationRound(updatedStops));
        history.setReason(ReoptimizationReasonEnum.USER_REQUESTED);
        history.setPreviousRouteSnapshot(previousSnapshot);
        history.setNewRouteSnapshot(toSnapshot(updatedStops));
        history.setTriggeredByUser(triggeredByUser);
        history.setNote(request.getNote());
        historyRepository.save(history);

        notificationService.sendToUser(
                routePlan.getUser().getId(),
                NotificationEventTypeEnum.ROUTE_REOPTIMIZED,
                "Rota güncellendi",
                routePlan.getTitle() + " için durak sırası yeniden oluşturuldu.",
                Map.of(
                        "routePlanId", routePlan.getId(),
                        "event", "ROUTE_REOPTIMIZED",
                        "optimizationRound", history.getOptimizationRound()
                )
        );
        return updatedStops;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteReoptimizationHistoryEntity> getRouteHistory(UUID routePlanId) {
        return historyRepository.findByRoutePlanIdOrderByDateOfRecordedDesc(routePlanId);
    }

    private boolean isCompletedLike(RouteStopEntity stop) {
        return stop.getStopStatus() == RouteStopStatusEnum.DELIVERED;
    }

    private boolean shouldParticipate(RouteStopEntity stop,
                                      boolean includeSkipped,
                                      boolean includeFailed,
                                      boolean includePostponed) {
        if (stop.getStopStatus() == RouteStopStatusEnum.DELIVERED) {
            return false;
        }
        if (stop.getStopStatus() == RouteStopStatusEnum.PENDING
                || stop.getStopStatus() == RouteStopStatusEnum.NAVIGATING
                || stop.getStopStatus() == RouteStopStatusEnum.ARRIVED) {
            return true;
        }
        if (stop.getStopStatus() == RouteStopStatusEnum.SKIPPED) {
            return includeSkipped;
        }
        if (stop.getStopStatus() == RouteStopStatusEnum.FAILED) {
            return includeFailed;
        }
        if (stop.getStopStatus() == RouteStopStatusEnum.POSTPONED) {
            return includePostponed;
        }
        return false;
    }

    private Integer resolveOptimizationRound(List<RouteStopEntity> stops) {
        return stops.stream()
                .map(RouteStopEntity::getOptimizationRound)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(1);
    }

    private String toSnapshot(List<RouteStopEntity> stops) {
        List<Map<String, Object>> snapshot = new ArrayList<>();

        for (RouteStopEntity stop : stops) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("stopId", stop.getId() == null ? null : stop.getId().toString());
            item.put("sequenceNo", stop.getSequenceNo());
            item.put("previousSequenceNo", stop.getPreviousSequenceNo());
            item.put("status", stop.getStopStatus() == null ? null : stop.getStopStatus().name());
            item.put("priorityNo", stop.getPriorityNo());
            item.put("address", stop.getRawAddress());
            snapshot.add(item);
        }

        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Snapshot oluşturulamadı", e);
        }
    }

    private Timestamp now() {
        return Timestamp.valueOf(ZonedDateTime.now(ZoneId.of("Europe/Istanbul")).toLocalDateTime());
    }
}