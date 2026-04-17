package com.routeplanner.backend.Repository;

import com.routeplanner.backend.Entity.RouteExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RouteExecutionRepository extends JpaRepository<RouteExecutionEntity, UUID> {
    Optional<RouteExecutionEntity> findByRoutePlanId(UUID routePlanId);
}
