package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.request.ReoptimizeRouteRequest;
import com.routeplanner.backend.entity.RouteReoptimizationHistoryEntity;
import com.routeplanner.backend.entity.RouteStopEntity;

import java.util.List;
import java.util.UUID;

public interface RouteOptimizationService {
    List<RouteStopEntity> reoptimizeRoute(UUID routePlanId, UUID triggeredByUserId, ReoptimizeRouteRequest request);
    List<RouteReoptimizationHistoryEntity> getRouteHistory(UUID routePlanId);
}
