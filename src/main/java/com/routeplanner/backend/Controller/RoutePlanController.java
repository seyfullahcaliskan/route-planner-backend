package com.routeplanner.backend.Controller;

import com.routeplanner.backend.DTO.Request.CreateRoutePlanRequest;
import com.routeplanner.backend.DTO.Request.CreateRouteStopRequest;
import com.routeplanner.backend.DTO.Response.RoutePlanResponse;
import com.routeplanner.backend.DTO.Response.RouteStopResponse;
import com.routeplanner.backend.Entity.RoutePlanEntity;
import com.routeplanner.backend.Entity.RouteStopEntity;
import com.routeplanner.backend.Mapper.RoutePlanMapper;
import com.routeplanner.backend.Mapper.RouteStopMapper;
import com.routeplanner.backend.Service.RoutePlanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/route-plans")
public class RoutePlanController {

    private final RoutePlanService routePlanService;

    public RoutePlanController(RoutePlanService routePlanService) {
        this.routePlanService = routePlanService;
    }

    @PostMapping
    public RoutePlanResponse createRoutePlan(@Valid @RequestBody CreateRoutePlanRequest request) {
        return RoutePlanMapper.toResponse(routePlanService.createRoutePlan(request));
    }

    @GetMapping("/user/{userId}")
    public List<RoutePlanResponse> listUserRoutes(@PathVariable UUID userId) {
        return routePlanService.listUserRoutes(userId)
                .stream()
                .map(RoutePlanMapper::toResponse)
                .toList();
    }

    @PostMapping("/{routePlanId}/stops")
    public List<RouteStopResponse> addStops(@PathVariable UUID routePlanId,
                                            @Valid @RequestBody List<CreateRouteStopRequest> requests) {
        return routePlanService.addStops(routePlanId, requests)
                .stream()
                .map(RouteStopMapper::toResponse)
                .toList();
    }

    @GetMapping("/{routePlanId}/stops")
    public List<RouteStopResponse> getStops(@PathVariable UUID routePlanId) {
        return routePlanService.getStops(routePlanId)
                .stream()
                .map(RouteStopMapper::toResponse)
                .toList();
    }
}