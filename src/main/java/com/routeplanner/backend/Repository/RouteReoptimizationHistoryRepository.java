package com.routeplanner.backend.Repository;

import com.routeplanner.backend.Entity.RouteReoptimizationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RouteReoptimizationHistoryRepository extends JpaRepository<RouteReoptimizationHistoryEntity, UUID> {
    List<RouteReoptimizationHistoryEntity> findByRoutePlanIdOrderByDateOfRecordedDesc(UUID routePlanId);
}
