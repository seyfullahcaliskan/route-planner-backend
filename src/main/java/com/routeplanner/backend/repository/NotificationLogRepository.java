package com.routeplanner.backend.repository;

import com.routeplanner.backend.entity.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, UUID> {
    List<NotificationLogEntity> findByUserIdOrderByDateOfRecordedDesc(UUID userId);
}