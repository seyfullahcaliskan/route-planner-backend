package com.routeplanner.backend.Service;

import com.routeplanner.backend.DTO.Request.CreateUserSavedPlaceRequest;
import com.routeplanner.backend.DTO.Response.UserSavedPlaceResponse;

import java.util.List;
import java.util.UUID;

public interface UserSavedPlaceService {

    UserSavedPlaceResponse create(CreateUserSavedPlaceRequest request);

    List<UserSavedPlaceResponse> getUserPlaces(UUID userId);

}
