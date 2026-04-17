package com.routeplanner.backend.Mapper;

import com.routeplanner.backend.DTO.Response.RoutePlanResponse;
import com.routeplanner.backend.Entity.RoutePlanEntity;

public class RoutePlanMapper {

    public static RoutePlanResponse toResponse(RoutePlanEntity entity) {
        RoutePlanResponse response = new RoutePlanResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUser().getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setRouteDate(entity.getRouteDate());
        response.setStartLatitude(entity.getStartLatitude());
        response.setStartLongitude(entity.getStartLongitude());
        response.setStartAddress(entity.getStartAddress());
        response.setEndLatitude(entity.getEndLatitude());
        response.setEndLongitude(entity.getEndLongitude());
        response.setEndAddress(entity.getEndAddress());
        response.setUseTolls(entity.getUseTolls());
        response.setUseHighways(entity.getUseHighways());
        response.setUseTraffic(entity.getUseTraffic());
        response.setOptimizationType(entity.getOptimizationType());
        response.setNavigationProvider(entity.getNavigationProvider());
        response.setPlanStatus(entity.getPlanStatus());
        response.setTotalStopCount(entity.getTotalStopCount());
        response.setCompletedStopCount(entity.getCompletedStopCount());
        response.setFailedStopCount(entity.getFailedStopCount());
        response.setSkippedStopCount(entity.getSkippedStopCount());
        response.setEstimatedTotalDistanceMeters(entity.getEstimatedTotalDistanceMeters());
        response.setEstimatedTotalDurationSeconds(entity.getEstimatedTotalDurationSeconds());
        response.setActualTotalDistanceMeters(entity.getActualTotalDistanceMeters());
        response.setActualTotalDurationSeconds(entity.getActualTotalDurationSeconds());
        response.setLastOptimizedAt(entity.getLastOptimizedAt());
        response.setLastStartedAt(entity.getLastStartedAt());
        response.setCompletedAt(entity.getCompletedAt());
        return response;
    }
}