package com.routeplanner.backend.controller;

import com.routeplanner.backend.dto.request.ReoptimizeRouteRequest;
import com.routeplanner.backend.dto.response.RouteReoptimizationHistoryResponse;
import com.routeplanner.backend.dto.response.RouteStopResponse;
import com.routeplanner.backend.mapper.RouteReoptimizationHistoryMapper;
import com.routeplanner.backend.mapper.RouteStopMapper;
import com.routeplanner.backend.service.RouteOptimizationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/route-plans")
public class RouteOptimizationController {

    private final RouteOptimizationService routeOptimizationService;

    public RouteOptimizationController(RouteOptimizationService routeOptimizationService) {
        this.routeOptimizationService = routeOptimizationService;
    }

    @PostMapping("/{routePlanId}/reoptimize")
    public List<RouteStopResponse> reoptimizeRoute(@PathVariable UUID routePlanId,
                                                   @RequestParam(required = false) UUID triggeredByUserId,
                                                   @RequestBody ReoptimizeRouteRequest request) {
        return routeOptimizationService.reoptimizeRoute(routePlanId, triggeredByUserId, request)
                .stream()
                .map(RouteStopMapper::toResponse)
                .toList();
    }

    @GetMapping("/{routePlanId}/optimization-history")
    public List<RouteReoptimizationHistoryResponse> getOptimizationHistory(@PathVariable UUID routePlanId) {
        return routeOptimizationService.getRouteHistory(routePlanId)
                .stream()
                .map(RouteReoptimizationHistoryMapper::toResponse)
                .toList();
    }
}
