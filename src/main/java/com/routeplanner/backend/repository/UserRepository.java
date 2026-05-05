package com.routeplanner.backend.repository;

import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.enums.AuthProviderEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByAuthProviderAndProviderId(AuthProviderEnum authProvider, String providerId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsername(String username);
}