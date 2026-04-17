package com.routeplanner.backend.Entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "route_execution")
public class RouteExecutionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_plan_id", nullable = false)
    private RoutePlanEntity routePlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_stop_id")
    private RouteStopEntity currentStop;

    @Column(name = "started_at")
    private Timestamp startedAt;

    @Column(name = "paused_at")
    private Timestamp pausedAt;

    @Column(name = "resumed_at")
    private Timestamp resumedAt;

    @Column(name = "finished_at")
    private Timestamp finishedAt;

    @Column(name = "completed_stop_count", nullable = false)
    private Integer completedStopCount = 0;

    @Column(name = "failed_stop_count", nullable = false)
    private Integer failedStopCount = 0;

    @Column(name = "skipped_stop_count", nullable = false)
    private Integer skippedStopCount = 0;

    @Column(name = "actual_distance_meters")
    private Long actualDistanceMeters;

    @Column(name = "actual_duration_seconds")
    private Long actualDurationSeconds;

    public RoutePlanEntity getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(RoutePlanEntity routePlan) {
        this.routePlan = routePlan;
    }

    public RouteStopEntity getCurrentStop() {
        return currentStop;
    }

    public void setCurrentStop(RouteStopEntity currentStop) {
        this.currentStop = currentStop;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public Timestamp getPausedAt() {
        return pausedAt;
    }

    public void setPausedAt(Timestamp pausedAt) {
        this.pausedAt = pausedAt;
    }

    public Timestamp getResumedAt() {
        return resumedAt;
    }

    public void setResumedAt(Timestamp resumedAt) {
        this.resumedAt = resumedAt;
    }

    public Timestamp getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Timestamp finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getCompletedStopCount() {
        return completedStopCount;
    }

    public void setCompletedStopCount(Integer completedStopCount) {
        this.completedStopCount = completedStopCount;
    }

    public Integer getFailedStopCount() {
        return failedStopCount;
    }

    public void setFailedStopCount(Integer failedStopCount) {
        this.failedStopCount = failedStopCount;
    }

    public Integer getSkippedStopCount() {
        return skippedStopCount;
    }

    public void setSkippedStopCount(Integer skippedStopCount) {
        this.skippedStopCount = skippedStopCount;
    }

    public Long getActualDistanceMeters() {
        return actualDistanceMeters;
    }

    public void setActualDistanceMeters(Long actualDistanceMeters) {
        this.actualDistanceMeters = actualDistanceMeters;
    }

    public Long getActualDurationSeconds() {
        return actualDurationSeconds;
    }

    public void setActualDurationSeconds(Long actualDurationSeconds) {
        this.actualDurationSeconds = actualDurationSeconds;
    }
}