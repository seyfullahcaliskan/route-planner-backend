package com.routeplanner.backend.Service;

import com.routeplanner.backend.DTO.Request.CreateRoutePlanRequest;
import com.routeplanner.backend.DTO.Request.CreateRouteStopRequest;
import com.routeplanner.backend.DTO.Response.RouteStopResponse;
import com.routeplanner.backend.Entity.RoutePlanEntity;
import com.routeplanner.backend.Entity.RouteStopEntity;

import java.util.List;
import java.util.UUID;

public interface RoutePlanService {
    RoutePlanEntity createRoutePlan(CreateRoutePlanRequest request);
    List<RoutePlanEntity> listUserRoutes(UUID userId);
    List<RouteStopEntity> addStops(UUID routePlanId, List<CreateRouteStopRequest> requests);
    List<RouteStopEntity> getStops(UUID routePlanId);
}