package com.routeplanner.backend.Entity;

import com.routeplanner.backend.Enums.ReoptimizationReasonEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "route_reoptimization_history")
public class RouteReoptimizationHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_plan_id", nullable = false)
    private RoutePlanEntity routePlan;

    @Column(name = "optimization_round", nullable = false)
    private Integer optimizationRound;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 50)
    private ReoptimizationReasonEnum reason;

    @Column(name = "previous_route_snapshot", nullable = false, columnDefinition = "jsonb")
    private String previousRouteSnapshot;

    @Column(name = "new_route_snapshot", nullable = false, columnDefinition = "jsonb")
    private String newRouteSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_by_user_id")
    private UserEntity triggeredByUser;

    @Column(name = "note", length = 1000)
    private String note;

    public RoutePlanEntity getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(RoutePlanEntity routePlan) {
        this.routePlan = routePlan;
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

    public UserEntity getTriggeredByUser() {
        return triggeredByUser;
    }

    public void setTriggeredByUser(UserEntity triggeredByUser) {
        this.triggeredByUser = triggeredByUser;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
