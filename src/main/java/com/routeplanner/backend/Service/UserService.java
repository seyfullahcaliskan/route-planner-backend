package com.routeplanner.backend.Service;

import com.routeplanner.backend.DTO.Request.CreateUserRequest;
import com.routeplanner.backend.Entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserEntity createUser(CreateUserRequest request);
    UserEntity getUser(UUID id);
    List<UserEntity> listUsers();
}
