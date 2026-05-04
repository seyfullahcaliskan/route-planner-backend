package com.routeplanner.backend.mapper;

import com.routeplanner.backend.dto.response.RouteReoptimizationHistoryResponse;
import com.routeplanner.backend.entity.RouteReoptimizationHistoryEntity;

public class RouteReoptimizationHistoryMapper {

    public static RouteReoptimizationHistoryResponse toResponse(RouteReoptimizationHistoryEntity entity) {
        RouteReoptimizationHistoryResponse response = new RouteReoptimizationHistoryResponse();
        response.setId(entity.getId());
        response.setRoutePlanId(entity.getRoutePlan().getId());
        response.setOptimizationRound(entity.getOptimizationRound());
        response.setReason(entity.getReason());
        response.setPreviousRouteSnapshot(entity.getPreviousRouteSnapshot());
        response.setNewRouteSnapshot(entity.getNewRouteSnapshot());
        response.setNote(entity.getNote());
        return response;
    }
}
