package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.request.CreateUserRequest;
import com.routeplanner.backend.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserEntity createUser(CreateUserRequest request);
    UserEntity getUser(UUID id);
    List<UserEntity> listUsers();
}
