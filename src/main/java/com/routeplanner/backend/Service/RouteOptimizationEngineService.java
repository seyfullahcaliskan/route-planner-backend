package com.routeplanner.backend.Service;

import com.routeplanner.backend.Entity.RoutePlanEntity;
import com.routeplanner.backend.Entity.RouteStopEntity;

import java.util.List;

public interface RouteOptimizationEngineService {
    List<RouteStopEntity> optimize(RoutePlanEntity routePlan, List<RouteStopEntity> stops);
}