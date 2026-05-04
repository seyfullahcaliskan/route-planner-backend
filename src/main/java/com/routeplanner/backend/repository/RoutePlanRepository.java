package com.routeplanner.backend.repository;

import com.routeplanner.backend.entity.RoutePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoutePlanRepository extends JpaRepository<RoutePlanEntity, UUID> {
    List<RoutePlanEntity> findByUserIdOrderByDateOfRecordedDesc(UUID userId);
}
