package com.routeplanner.backend.Repository;

import com.routeplanner.backend.Entity.RouteStopEntity;
import com.routeplanner.backend.Enums.RouteStopStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RouteStopRepository extends JpaRepository<RouteStopEntity, UUID> {
    List<RouteStopEntity> findByRoutePlanIdOrderBySequenceNoAsc(UUID routePlanId);
    long countByRoutePlanIdAndStopStatus(UUID routePlanId, RouteStopStatusEnum stopStatus);
}