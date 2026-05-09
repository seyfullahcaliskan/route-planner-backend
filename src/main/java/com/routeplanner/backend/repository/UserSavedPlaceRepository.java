package com.routeplanner.backend.repository;

import com.routeplanner.backend.entity.UserSavedPlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSavedPlaceRepository extends JpaRepository<UserSavedPlaceEntity, UUID> {

    List<UserSavedPlaceEntity> findByUserIdOrderByDateOfRecordedDesc(UUID userId);

    /** Geriye dönük uyumluluk: eski controller hâlâ kullanıyor olabilir. */
    default List<UserSavedPlaceEntity> findByUserId(UUID userId) {
        return findByUserIdOrderByDateOfRecordedDesc(userId);
    }

    Optional<UserSavedPlaceEntity> findByIdAndUserId(UUID id, UUID userId);

    List<UserSavedPlaceEntity> findByUserIdAndIsDefaultStartTrue(UUID userId);

    List<UserSavedPlaceEntity> findByUserIdAndIsDefaultEndTrue(UUID userId);
}
