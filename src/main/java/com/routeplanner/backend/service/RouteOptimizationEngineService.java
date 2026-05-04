package com.routeplanner.backend.service;

import com.routeplanner.backend.entity.RoutePlanEntity;
import com.routeplanner.backend.entity.RouteStopEntity;

import java.util.List;

public interface RouteOptimizationEngineService {
    List<RouteStopEntity> optimize(RoutePlanEntity routePlan, List<RouteStopEntity> stops);
}