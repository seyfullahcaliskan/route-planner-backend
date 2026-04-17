package com.routeplanner.backend.Service;

import com.routeplanner.backend.DTO.Request.ReoptimizeRouteRequest;
import com.routeplanner.backend.Entity.RouteReoptimizationHistoryEntity;
import com.routeplanner.backend.Entity.RouteStopEntity;

import java.util.List;
import java.util.UUID;

public interface RouteOptimizationService {
    List<RouteStopEntity> reoptimizeRoute(UUID routePlanId, UUID triggeredByUserId, ReoptimizeRouteRequest request);
    List<RouteReoptimizationHistoryEntity> getRouteHistory(UUID routePlanId);
}
