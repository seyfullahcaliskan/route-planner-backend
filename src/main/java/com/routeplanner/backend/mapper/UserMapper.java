package com.routeplanner.backend.mapper;

import com.routeplanner.backend.dto.response.UserResponse;
import com.routeplanner.backend.entity.UserEntity;

public class UserMapper {

    public static UserResponse toResponse(UserEntity entity) {
        UserResponse response = new UserResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setSurname(entity.getSurname());
        response.setUsername(entity.getUsername());
        response.setEmail(entity.getEmail());
        response.setPhoneNumber(entity.getPhoneNumber());
        response.setRole(entity.getRole());
        response.setCompanyName(entity.getCompanyName());
        response.setAuthProvider(entity.getAuthProvider());
        response.setAvatarUrl(entity.getAvatarUrl());
        response.setEmailVerified(entity.getEmailVerified());
        return response;
    }
}
