package com.routeplanner.backend.Repository;

import com.routeplanner.backend.Entity.NavigationEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NavigationEventRepository extends JpaRepository<NavigationEventEntity, UUID> {
    List<NavigationEventEntity> findByRoutePlanIdOrderByDateOfRecordedDesc(UUID routePlanId);
}
