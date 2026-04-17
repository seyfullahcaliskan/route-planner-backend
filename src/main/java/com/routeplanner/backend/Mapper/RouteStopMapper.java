package com.routeplanner.backend.Mapper;

import com.routeplanner.backend.DTO.Response.RouteStopResponse;
import com.routeplanner.backend.Entity.RouteStopEntity;

public class RouteStopMapper {

    public static RouteStopResponse toResponse(RouteStopEntity entity) {
        RouteStopResponse response = new RouteStopResponse();
        response.setId(entity.getId());
        response.setExternalReference(entity.getExternalReference());
        response.setCustomerName(entity.getCustomerName());
        response.setCustomerPhone(entity.getCustomerPhone());
        response.setRawAddress(entity.getRawAddress());
        response.setNormalizedAddress(entity.getNormalizedAddress());
        response.setLatitude(entity.getLatitude());
        response.setLongitude(entity.getLongitude());
        response.setSequenceNo(entity.getSequenceNo());
        response.setPreviousSequenceNo(entity.getPreviousSequenceNo());
        response.setOptimizationRound(entity.getOptimizationRound());
        response.setPriorityNo(entity.getPriorityNo());
        response.setDeliveryNote(entity.getDeliveryNote());
        response.setEstimatedArrivalTime(entity.getEstimatedArrivalTime());
        response.setActualArrivalTime(entity.getActualArrivalTime());
        response.setDeliveredAt(entity.getDeliveredAt());
        response.setStopStatus(entity.getStopStatus());
        response.setIsLocked(entity.getIsLocked());
        response.setIsCancelled(entity.getIsCancelled());
        response.setNavigationUrl(entity.getNavigationUrl());
        return response;
    }
}