package com.routeplanner.backend.Mapper;

import com.routeplanner.backend.DTO.Response.RouteReoptimizationHistoryResponse;
import com.routeplanner.backend.Entity.RouteReoptimizationHistoryEntity;

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
