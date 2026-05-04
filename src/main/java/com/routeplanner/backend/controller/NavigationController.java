package com.routeplanner.backend.controller;

import com.routeplanner.backend.dto.response.NavigationUrlResponse;
import com.routeplanner.backend.enums.NavigationProviderEnum;
import com.routeplanner.backend.service.NavigationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/route-stops")
public class NavigationController {

    private final NavigationService navigationService;

    public NavigationController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @GetMapping("/{routeStopId}/navigation-url")
    public NavigationUrlResponse getNavigationUrl(@PathVariable UUID routeStopId,
                                                  @RequestParam(required = false) NavigationProviderEnum provider) {
        return navigationService.buildNavigationUrl(routeStopId, provider);
    }
}