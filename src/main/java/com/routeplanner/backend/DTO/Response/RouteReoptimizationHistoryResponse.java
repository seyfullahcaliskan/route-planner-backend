package com.routeplanner.backend.DTO.Response;

import com.routeplanner.backend.Enums.ReoptimizationReasonEnum;

import java.util.UUID;

public class RouteReoptimizationHistoryResponse {

    private UUID id;
    private UUID routePlanId;
    private Integer optimizationRound;
    private ReoptimizationReasonEnum reason;
    private String previousRouteSnapshot;
    private String newRouteSnapshot;
    private String note;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRoutePlanId() {
        return routePlanId;
    }

    public void setRoutePlanId(UUID routePlanId) {
        this.routePlanId = routePlanId;
    }

    public Integer getOptimizationRound() {
        return optimizationRound;
    }

    public void setOptimizationRound(Integer optimizationRound) {
        this.optimizationRound = optimizationRound;
    }

    public ReoptimizationReasonEnum getReason() {
        return reason;
    }

    public void setReason(ReoptimizationReasonEnum reason) {
        this.reason = reason;
    }

    public String getPreviousRouteSnapshot() {
        return previousRouteSnapshot;
    }

    public void setPreviousRouteSnapshot(String previousRouteSnapshot) {
        this.previousRouteSnapshot = previousRouteSnapshot;
    }

    public String getNewRouteSnapshot() {
        return newRouteSnapshot;
    }

    public void setNewRouteSnapshot(String newRouteSnapshot) {
        this.newRouteSnapshot = newRouteSnapshot;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
