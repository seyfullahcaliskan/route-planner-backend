package com.routeplanner.backend.repository;

import com.routeplanner.backend.entity.NavigationEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NavigationEventRepository extends JpaRepository<NavigationEventEntity, UUID> {
    List<NavigationEventEntity> findByRoutePlanIdOrderByDateOfRecordedDesc(UUID routePlanId);
}
