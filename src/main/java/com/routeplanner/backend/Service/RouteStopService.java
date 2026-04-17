package com.routeplanner.backend.Service;

import com.routeplanner.backend.Entity.RouteStopEntity;

import java.util.UUID;

public interface RouteStopService {
    RouteStopEntity markDelivered(UUID routeStopId, String note);
    RouteStopEntity markSkipped(UUID routeStopId, String note);
    RouteStopEntity markFailed(UUID routeStopId, String note);
}
