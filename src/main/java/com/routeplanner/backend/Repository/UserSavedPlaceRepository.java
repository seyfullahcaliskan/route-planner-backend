package com.routeplanner.backend.Repository;

import com.routeplanner.backend.Entity.UserSavedPlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserSavedPlaceRepository extends JpaRepository<UserSavedPlaceEntity, UUID> {

    List<UserSavedPlaceEntity> findByUserId(UUID userId);

}
