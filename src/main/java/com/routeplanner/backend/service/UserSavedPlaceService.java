package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.request.CreateUserSavedPlaceRequest;
import com.routeplanner.backend.dto.response.UserSavedPlaceResponse;

import java.util.List;
import java.util.UUID;

public interface UserSavedPlaceService {

    UserSavedPlaceResponse create(CreateUserSavedPlaceRequest request);

    List<UserSavedPlaceResponse> getUserPlaces(UUID userId);

}
