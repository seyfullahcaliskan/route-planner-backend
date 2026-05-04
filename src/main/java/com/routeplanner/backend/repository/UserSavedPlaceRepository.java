package com.routeplanner.backend.repository;

import com.routeplanner.backend.entity.UserSavedPlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserSavedPlaceRepository extends JpaRepository<UserSavedPlaceEntity, UUID> {

    List<UserSavedPlaceEntity> findByUserId(UUID userId);

}
