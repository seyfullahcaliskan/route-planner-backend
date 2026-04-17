package com.routeplanner.backend.Entity;

import com.routeplanner.backend.Enums.NavigationProviderEnum;
import com.routeplanner.backend.Enums.RouteOptimizationTypeEnum;
import com.routeplanner.backend.Enums.RoutePlanStatusEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "route_plan")
public class RoutePlanEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "route_date")
    private LocalDate routeDate;

    @Column(name = "start_latitude", precision = 10, scale = 7)
    private BigDecimal startLatitude;

    @Column(name = "start_longitude", precision = 10, scale = 7)
    private BigDecimal startLongitude;

    @Column(name = "start_address", length = 500)
    private String startAddress;

    @Column(name = "end_latitude", precision = 10, scale = 7)
    private BigDecimal endLatitude;

    @Column(name = "end_longitude", precision = 10, scale = 7)
    private BigDecimal endLongitude;

    @Column(name = "end_address", length = 500)
    private String endAddress;

    @Column(name = "use_tolls", nullable = false)
    private Boolean useTolls = false;

    @Column(name = "use_highways", nullable = false)
    private Boolean useHighways = true;

    @Column(name = "use_traffic", nullable = false)
    private Boolean useTraffic = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "optimization_type", nullable = false, length = 30)
    private RouteOptimizationTypeEnum optimizationType = RouteOptimizationTypeEnum.FASTEST;

    @Enumerated(EnumType.STRING)
    @Column(name = "navigation_provider", nullable = false, length = 30)
    private NavigationProviderEnum navigationProvider = NavigationProviderEnum.GOOGLE_MAPS;

    @Column(name = "total_stop_count", nullable = false)
    private Integer totalStopCount = 0;

    @Column(name = "completed_stop_count", nullable = false)
    private Integer completedStopCount = 0;

    @Column(name = "failed_stop_count", nullable = false)
    private Integer failedStopCount = 0;

    @Column(name = "skipped_stop_count", nullable = false)
    private Integer skippedStopCount = 0;

    @Column(name = "estimated_total_distance_meters")
    private Long estimatedTotalDistanceMeters;

    @Column(name = "estimated_total_duration_seconds")
    private Long estimatedTotalDurationSeconds;

    @Column(name = "actual_total_distance_meters")
    private Long actualTotalDistanceMeters;

    @Column(name = "actual_total_duration_seconds")
    private Long actualTotalDurationSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_status", nullable = false, length = 30)
    private RoutePlanStatusEnum planStatus = RoutePlanStatusEnum.DRAFT;

    @Column(name = "last_optimized_at")
    private Timestamp lastOptimizedAt;

    @Column(name = "last_started_at")
    private Timestamp lastStartedAt;

    @Column(name = "completed_at")
    private Timestamp completedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "routePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNo asc")
    private List<RouteStopEntity> stops = new ArrayList<>();

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getRouteDate() {
        return routeDate;
    }

    public void setRouteDate(LocalDate routeDate) {
        this.routeDate = routeDate;
    }

    public BigDecimal getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(BigDecimal startLatitude) {
        this.startLatitude = startLatitude;
    }

    public BigDecimal getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(BigDecimal startLongitude) {
        this.startLongitude = startLongitude;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public BigDecimal getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(BigDecimal endLatitude) {
        this.endLatitude = endLatitude;
    }

    public BigDecimal getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(BigDecimal endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public Boolean getUseTolls() {
        return useTolls;
    }

    public void setUseTolls(Boolean useTolls) {
        this.useTolls = useTolls;
    }

    public Boolean getUseHighways() {
        return useHighways;
    }

    public void setUseHighways(Boolean useHighways) {
        this.useHighways = useHighways;
    }

    public Boolean getUseTraffic() {
        return useTraffic;
    }

    public void setUseTraffic(Boolean useTraffic) {
        this.useTraffic = useTraffic;
    }

    public RouteOptimizationTypeEnum getOptimizationType() {
        return optimizationType;
    }

    public void setOptimizationType(RouteOptimizationTypeEnum optimizationType) {
        this.optimizationType = optimizationType;
    }

    public NavigationProviderEnum getNavigationProvider() {
        return navigationProvider;
    }

    public void setNavigationProvider(NavigationProviderEnum navigationProvider) {
        this.navigationProvider = navigationProvider;
    }

    public Integer getTotalStopCount() {
        return totalStopCount;
    }

    public void setTotalStopCount(Integer totalStopCount) {
        this.totalStopCount = totalStopCount;
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

    public Long getEstimatedTotalDistanceMeters() {
        return estimatedTotalDistanceMeters;
    }

    public void setEstimatedTotalDistanceMeters(Long estimatedTotalDistanceMeters) {
        this.estimatedTotalDistanceMeters = estimatedTotalDistanceMeters;
    }

    public Long getEstimatedTotalDurationSeconds() {
        return estimatedTotalDurationSeconds;
    }

    public void setEstimatedTotalDurationSeconds(Long estimatedTotalDurationSeconds) {
        this.estimatedTotalDurationSeconds = estimatedTotalDurationSeconds;
    }

    public Long getActualTotalDistanceMeters() {
        return actualTotalDistanceMeters;
    }

    public void setActualTotalDistanceMeters(Long actualTotalDistanceMeters) {
        this.actualTotalDistanceMeters = actualTotalDistanceMeters;
    }

    public Long getActualTotalDurationSeconds() {
        return actualTotalDurationSeconds;
    }

    public void setActualTotalDurationSeconds(Long actualTotalDurationSeconds) {
        this.actualTotalDurationSeconds = actualTotalDurationSeconds;
    }

    public RoutePlanStatusEnum getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(RoutePlanStatusEnum planStatus) {
        this.planStatus = planStatus;
    }

    public Timestamp getLastOptimizedAt() {
        return lastOptimizedAt;
    }

    public void setLastOptimizedAt(Timestamp lastOptimizedAt) {
        this.lastOptimizedAt = lastOptimizedAt;
    }

    public Timestamp getLastStartedAt() {
        return lastStartedAt;
    }

    public void setLastStartedAt(Timestamp lastStartedAt) {
        this.lastStartedAt = lastStartedAt;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public List<RouteStopEntity> getStops() {
        return stops;
    }

    public void setStops(List<RouteStopEntity> stops) {
        this.stops = stops;
    }
}
