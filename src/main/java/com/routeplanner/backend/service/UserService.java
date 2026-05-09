package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.request.ChangePasswordRequest;
import com.routeplanner.backend.dto.request.CreateUserRequest;
import com.routeplanner.backend.dto.request.UpdateUserProfileRequest;
import com.routeplanner.backend.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserEntity createUser(CreateUserRequest request);
    UserEntity getUser(UUID id);
    List<UserEntity> listUsers();

    /** Profil güncelleme — null alana dokunulmaz. */
    UserEntity updateProfile(UUID userId, UpdateUserProfileRequest request);

    /** Şifre değiştirme — eski şifre doğrulanır; OAuth kullanıcıları reddedilir. */
    void changePassword(UUID userId, ChangePasswordRequest request);
}
