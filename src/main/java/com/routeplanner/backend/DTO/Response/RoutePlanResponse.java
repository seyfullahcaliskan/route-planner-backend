package com.routeplanner.backend.DTO.Response;

import com.routeplanner.backend.Enums.NavigationProviderEnum;
import com.routeplanner.backend.Enums.RouteOptimizationTypeEnum;
import com.routeplanner.backend.Enums.RoutePlanStatusEnum;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public class RoutePlanResponse {

    private UUID id;
    private UUID userId;
    private String title;
    private String description;
    private LocalDate routeDate;

    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private String startAddress;

    private BigDecimal endLatitude;
    private BigDecimal endLongitude;
    private String endAddress;

    private Boolean useTolls;
    private Boolean useHighways;
    private Boolean useTraffic;

    private RouteOptimizationTypeEnum optimizationType;
    private NavigationProviderEnum navigationProvider;
    private RoutePlanStatusEnum planStatus;

    private Integer totalStopCount;
    private Integer completedStopCount;
    private Integer failedStopCount;
    private Integer skippedStopCount;

    private Long estimatedTotalDistanceMeters;
    private Long estimatedTotalDurationSeconds;
    private Long actualTotalDistanceMeters;
    private Long actualTotalDurationSeconds;

    private Timestamp lastOptimizedAt;
    private Timestamp lastStartedAt;
    private Timestamp completedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public RoutePlanStatusEnum getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(RoutePlanStatusEnum planStatus) {
        this.planStatus = planStatus;
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
}