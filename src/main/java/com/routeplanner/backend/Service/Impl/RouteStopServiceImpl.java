package com.routeplanner.backend.Service.Impl;

import com.routeplanner.backend.Entity.RouteExecutionEntity;
import com.routeplanner.backend.Entity.RoutePlanEntity;
import com.routeplanner.backend.Entity.RouteStopEntity;
import com.routeplanner.backend.Enums.RoutePlanStatusEnum;
import com.routeplanner.backend.Enums.RouteStopStatusEnum;
import com.routeplanner.backend.Repository.RouteExecutionRepository;
import com.routeplanner.backend.Repository.RoutePlanRepository;
import com.routeplanner.backend.Repository.RouteStopRepository;
import com.routeplanner.backend.Service.RouteStopService;
import com.routeplanner.backend.Enums.NotificationEventTypeEnum;
import com.routeplanner.backend.Service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class RouteStopServiceImpl implements RouteStopService {

    private final RouteStopRepository routeStopRepository;
    private final RoutePlanRepository routePlanRepository;
    private final RouteExecutionRepository routeExecutionRepository;
    private final NotificationService notificationService;

    public RouteStopServiceImpl(RouteStopRepository routeStopRepository,
                                RoutePlanRepository routePlanRepository,
                                RouteExecutionRepository routeExecutionRepository, NotificationService notificationService) {
        this.routeStopRepository = routeStopRepository;
        this.routePlanRepository = routePlanRepository;
        this.routeExecutionRepository = routeExecutionRepository;
        this.notificationService = notificationService;
    }

    @Override
    public RouteStopEntity markDelivered(UUID routeStopId, String note) {
        RouteStopEntity stop = getStop(routeStopId);
        stop.setStopStatus(RouteStopStatusEnum.DELIVERED);
        stop.setDeliveredAt(now());
        stop.setActualArrivalTime(now());
        appendNote(stop, note);

        RouteStopEntity saved = routeStopRepository.save(stop);
        refreshRoutePlanCounters(saved.getRoutePlan().getId());
        upsertExecution(saved.getRoutePlan(), saved);

        notificationService.sendToUser(
                saved.getRoutePlan().getUser().getId(),
                NotificationEventTypeEnum.STOP_DELIVERED,
                "Teslimat tamamlandı",
                (saved.getCustomerName() == null ? "Bir durak" : saved.getCustomerName()) + " teslim edildi.",
                Map.of(
                        "routePlanId", saved.getRoutePlan().getId(),
                        "routeStopId", saved.getId(),
                        "event", "STOP_DELIVERED"
                )
        );
        return saved;
    }

    @Override
    public RouteStopEntity markSkipped(UUID routeStopId, String note) {
        RouteStopEntity stop = getStop(routeStopId);
        stop.setStopStatus(RouteStopStatusEnum.SKIPPED);
        appendNote(stop, note);

        RouteStopEntity saved = routeStopRepository.save(stop);
        refreshRoutePlanCounters(saved.getRoutePlan().getId());
        upsertExecution(saved.getRoutePlan(), findNextPendingStop(saved.getRoutePlan().getId()));

        notificationService.sendToUser(
                saved.getRoutePlan().getUser().getId(),
                NotificationEventTypeEnum.STOP_SKIPPED,
                "Durak atlandı",
                (saved.getCustomerName() == null ? "Bir durak" : saved.getCustomerName()) + " atlandı.",
                Map.of(
                        "routePlanId", saved.getRoutePlan().getId(),
                        "routeStopId", saved.getId(),
                        "event", "STOP_SKIPPED"
                )
        );
        return saved;
    }

    @Override
    public RouteStopEntity markFailed(UUID routeStopId, String note) {
        RouteStopEntity stop = getStop(routeStopId);
        stop.setStopStatus(RouteStopStatusEnum.FAILED);
        appendNote(stop, note);

        RouteStopEntity saved = routeStopRepository.save(stop);
        refreshRoutePlanCounters(saved.getRoutePlan().getId());
        upsertExecution(saved.getRoutePlan(), findNextPendingStop(saved.getRoutePlan().getId()));

        notificationService.sendToUser(
                saved.getRoutePlan().getUser().getId(),
                NotificationEventTypeEnum.STOP_FAILED,
                "Teslimat başarısız",
                (saved.getCustomerName() == null ? "Bir durak" : saved.getCustomerName()) + " başarısız olarak işaretlendi.",
                Map.of(
                        "routePlanId", saved.getRoutePlan().getId(),
                        "routeStopId", saved.getId(),
                        "event", "STOP_FAILED"
                )
        );
        return saved;
    }

    private RouteStopEntity getStop(UUID routeStopId) {
        return routeStopRepository.findById(routeStopId)
                .orElseThrow(() -> new RuntimeException("Route stop not found: " + routeStopId));
    }

    private void appendNote(RouteStopEntity stop, String note) {
        if (note == null || note.isBlank()) {
            return;
        }

        if (stop.getDeliveryNote() == null || stop.getDeliveryNote().isBlank()) {
            stop.setDeliveryNote(note);
        } else {
            stop.setDeliveryNote(stop.getDeliveryNote() + " | " + note);
        }
    }

    private Timestamp now() {
        return Timestamp.valueOf(ZonedDateTime.now(ZoneId.of("Europe/Istanbul")).toLocalDateTime());
    }

    private RouteStopEntity findNextPendingStop(UUID routePlanId) {
        List<RouteStopEntity> stops = routeStopRepository.findByRoutePlanIdOrderBySequenceNoAsc(routePlanId);
        return stops.stream()
                .filter(stop -> stop.getStopStatus() == RouteStopStatusEnum.PENDING
                        || stop.getStopStatus() == RouteStopStatusEnum.NAVIGATING
                        || stop.getStopStatus() == RouteStopStatusEnum.ARRIVED
                        || stop.getStopStatus() == RouteStopStatusEnum.POSTPONED)
                .findFirst()
                .orElse(null);
    }

    private void refreshRoutePlanCounters(UUID routePlanId) {
        RoutePlanEntity plan = routePlanRepository.findById(routePlanId)
                .orElseThrow(() -> new RuntimeException("Route plan not found: " + routePlanId));

        List<RouteStopEntity> stops = routeStopRepository.findByRoutePlanIdOrderBySequenceNoAsc(routePlanId);

        int completed = (int) stops.stream().filter(s -> s.getStopStatus() == RouteStopStatusEnum.DELIVERED).count();
        int failed = (int) stops.stream().filter(s -> s.getStopStatus() == RouteStopStatusEnum.FAILED).count();
        int skipped = (int) stops.stream().filter(s -> s.getStopStatus() == RouteStopStatusEnum.SKIPPED).count();

        plan.setCompletedStopCount(completed);
        plan.setFailedStopCount(failed);
        plan.setSkippedStopCount(skipped);
        plan.setTotalStopCount(stops.size());

        boolean allDone = stops.stream().allMatch(s ->
                s.getStopStatus() == RouteStopStatusEnum.DELIVERED
                        || s.getStopStatus() == RouteStopStatusEnum.FAILED
                        || s.getStopStatus() == RouteStopStatusEnum.SKIPPED);

        if (allDone && !stops.isEmpty()) {
            plan.setPlanStatus(RoutePlanStatusEnum.COMPLETED);
            plan.setCompletedAt(now());
        } else if (!stops.isEmpty()) {
            plan.setPlanStatus(RoutePlanStatusEnum.IN_PROGRESS);
            if (plan.getLastStartedAt() == null) {
                plan.setLastStartedAt(now());
            }
        }

        routePlanRepository.save(plan);
    }

    private void upsertExecution(RoutePlanEntity plan, RouteStopEntity currentStop) {
        RouteExecutionEntity execution = routeExecutionRepository.findByRoutePlanId(plan.getId())
                .orElseGet(() -> {
                    RouteExecutionEntity entity = new RouteExecutionEntity();
                    entity.setRoutePlan(plan);
                    entity.setStartedAt(now());
                    return entity;
                });

        execution.setCurrentStop(currentStop);
        execution.setCompletedStopCount(plan.getCompletedStopCount());
        execution.setFailedStopCount(plan.getFailedStopCount());
        execution.setSkippedStopCount(plan.getSkippedStopCount());

        if (plan.getPlanStatus() == RoutePlanStatusEnum.COMPLETED) {
            execution.setFinishedAt(now());
        }

        routeExecutionRepository.save(execution);
    }
}