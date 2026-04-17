package com.routeplanner.backend.Repository;

import com.routeplanner.backend.Entity.DeviceTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceTokenRepository extends JpaRepository<DeviceTokenEntity, UUID> {
    Optional<DeviceTokenEntity> findByExpoPushToken(String expoPushToken);
    List<DeviceTokenEntity> findByUserIdAndIsActiveTrue(UUID userId);
}