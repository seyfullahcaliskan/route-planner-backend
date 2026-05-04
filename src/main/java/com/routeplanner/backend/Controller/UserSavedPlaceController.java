package com.routeplanner.backend.Controller;

import com.routeplanner.backend.DTO.Request.CreateUserSavedPlaceRequest;
import com.routeplanner.backend.DTO.Response.UserSavedPlaceResponse;
import com.routeplanner.backend.Service.UserSavedPlaceService;
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
