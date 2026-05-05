package com.routeplanner.backend.repository;

import com.routeplanner.backend.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    List<RefreshTokenEntity> findByUserIdAndRevokedAtIsNull(UUID userId);

    @Modifying
    @Query("update RefreshTokenEntity r set r.revokedAt = :now where r.user.id = :userId and r.revokedAt is null")
    int revokeAllForUser(@Param("userId") UUID userId, @Param("now") Timestamp now);

    @Modifying
    @Query("delete from RefreshTokenEntity r where r.expiresAt < :cutoff")
    int deleteExpired(@Param("cutoff") Timestamp cutoff);
}