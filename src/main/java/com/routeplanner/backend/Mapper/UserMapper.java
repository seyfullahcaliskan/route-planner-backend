package com.routeplanner.backend.Mapper;

import com.routeplanner.backend.DTO.Response.UserResponse;
import com.routeplanner.backend.Entity.UserEntity;

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
        return response;
    }
}
