package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.request.AddStopsAndReoptimizeRequest;
import com.routeplanner.backend.dto.request.CreateRoutePlanRequest;
import com.routeplanner.backend.dto.request.CreateRouteStopRequest;
import com.routeplanner.backend.entity.RoutePlanEntity;
import com.routeplanner.backend.entity.RouteStopEntity;

import java.util.List;
import java.util.UUID;

public interface RoutePlanService {
    RoutePlanEntity createRoutePlan(CreateRoutePlanRequest request);
    List<RoutePlanEntity> listUserRoutes(UUID userId);
    List<RouteStopEntity> addStops(UUID routePlanId, List<CreateRouteStopRequest> requests);
    List<RouteStopEntity> getStops(UUID routePlanId);

    /**
     * Rotada yola çıkmışken yeni durak(lar) ekle ve hemen yeniden optimize et.
     * Tek transaction → yarıda kalmaz.
     */
    List<RouteStopEntity> addStopsAndReoptimize(UUID routePlanId, AddStopsAndReoptimizeRequest request);
}
