package com.routeplanner.backend.service;

import com.routeplanner.backend.entity.RouteStopEntity;

import java.util.UUID;

public interface RouteStopService {
    RouteStopEntity markDelivered(UUID routeStopId, String note);
    RouteStopEntity markSkipped(UUID routeStopId, String note);
    RouteStopEntity markFailed(UUID routeStopId, String note);
}
