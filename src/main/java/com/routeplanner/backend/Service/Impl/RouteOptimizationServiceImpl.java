package com.routeplanner.backend.Service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeplanner.backend.DTO.Request.ReoptimizeRouteRequest;
import com.routeplanner.backend.Entity.RoutePlanEntity;
import com.routeplanner.backend.Entity.RouteReoptimizationHistoryEntity;
import com.routeplanner.backend.Entity.RouteStopEntity;
import com.routeplanner.backend.Entity.UserEntity;
import com.routeplanner.backend.Enums.ReoptimizationReasonEnum;
import com.routeplanner.backend.Enums.RoutePlanStatusEnum;
import com.routeplanner.backend.Enums.RouteStopStatusEnum;
import com.routeplanner.backend.Repository.RoutePlanRepository;
import com.routeplanner.backend.Repository.RouteReoptimizationHistoryRepository;
import com.routeplanner.backend.Repository.RouteStopRepository;
import com.routeplanner.backend.Repository.UserRepository;
import com.routeplanner.backend.Service.RouteOptimizationService;
import com.routeplanner.backend.Enums.NotificationEventTypeEnum;
import com.routeplanner.backend.Service.NotificationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RouteOptimizationServiceImpl implements RouteOptimizationService {

    private final RoutePlanRepository routePlanRepository;
    private final RouteStopRepository routeStopRepository;
    private final RouteReoptimizationHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public RouteOptimizationServiceImpl(RoutePlanRepository routePlanRepository,
                                        RouteStopRepository routeStopRepository,
                                        RouteReoptimizationHistoryRepository historyRepository,
                                        UserRepository userRepository,
                                        ObjectMapper objectMapper, NotificationService notificationService) {
        this.routePlanRepository = routePlanRepository;
        this.routeStopRepository = routeStopRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
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

        boolean includeSkipped = Boolean.TRUE.equals(request.getIncludeSkippedStops());
        boolean includeFailed = Boolean.TRUE.equals(request.getIncludeFailedStops());
        boolean includePostponed = request.getIncludePostponedStops() == null || request.getIncludePostponedStops();

        List<RouteStopEntity> fixedStops = allStops.stream()
                .filter(this::isCompletedLike)
                .collect(Collectors.toList());

        List<RouteStopEntity> candidateStops = allStops.stream()
                .filter(stop -> shouldParticipate(stop, includeSkipped, includeFailed, includePostponed))
                .collect(Collectors.toList());

        candidateStops.sort(
                Comparator.comparing(RouteStopEntity::getPriorityNo, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(RouteStopEntity::getSequenceNo)
        );

        int maxFixedSequence = fixedStops.stream()
                .map(RouteStopEntity::getSequenceNo)
                .max(Integer::compareTo)
                .orElse(0);

        for (int i = 0; i < candidateStops.size(); i++) {
            RouteStopEntity stop = candidateStops.get(i);
            stop.setPreviousSequenceNo(stop.getSequenceNo());
            stop.setSequenceNo(maxFixedSequence + i + 1);
            stop.setOptimizationRound(stop.getOptimizationRound() == null ? 2 : stop.getOptimizationRound() + 1);

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
            item.put("stopId", stop.getId());
            item.put("sequenceNo", stop.getSequenceNo());
            item.put("previousSequenceNo", stop.getPreviousSequenceNo());
            item.put("status", stop.getStopStatus());
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