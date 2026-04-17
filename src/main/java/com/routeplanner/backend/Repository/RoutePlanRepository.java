package com.routeplanner.backend.Repository;

import com.routeplanner.backend.Entity.RoutePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoutePlanRepository extends JpaRepository<RoutePlanEntity, UUID> {
    List<RoutePlanEntity> findByUserIdOrderByDateOfRecordedDesc(UUID userId);
}
