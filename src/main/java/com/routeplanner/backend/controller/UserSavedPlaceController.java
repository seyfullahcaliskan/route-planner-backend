package com.routeplanner.backend.controller;

import com.routeplanner.backend.dto.request.CreateUserSavedPlaceRequest;
import com.routeplanner.backend.dto.response.UserSavedPlaceResponse;
import com.routeplanner.backend.service.UserSavedPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class UserSavedPlaceController {

    private final UserSavedPlaceService service;

    @PostMapping
    public UserSavedPlaceResponse create(@RequestBody CreateUserSavedPlaceRequest request) {
        return service.create(request);
    }

    @GetMapping("/{userId}")
    public List<UserSavedPlaceResponse> get(@PathVariable UUID userId) {
        return service.getUserPlaces(userId);
    }
}
