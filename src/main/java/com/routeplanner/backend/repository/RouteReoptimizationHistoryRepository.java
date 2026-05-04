package com.routeplanner.backend.repository;

import com.routeplanner.backend.entity.RouteReoptimizationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RouteReoptimizationHistoryRepository extends JpaRepository<RouteReoptimizationHistoryEntity, UUID> {
    List<RouteReoptimizationHistoryEntity> findByRoutePlanIdOrderByDateOfRecordedDesc(UUID routePlanId);
}
