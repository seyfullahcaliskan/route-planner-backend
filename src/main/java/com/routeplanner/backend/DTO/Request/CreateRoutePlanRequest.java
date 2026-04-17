package com.routeplanner.backend.DTO.Request;

import com.routeplanner.backend.Enums.NavigationProviderEnum;
import com.routeplanner.backend.Enums.RouteOptimizationTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateRoutePlanRequest {

    @NotNull
    private UUID userId;

    @NotBlank
    private String title;

    private String description;
    private LocalDate routeDate;

    private String startAddress;
    private BigDecimal startLatitude;
    private BigDecimal startLongitude;

    private String endAddress;
    private BigDecimal endLatitude;
    private BigDecimal endLongitude;

    private Boolean useTolls;
    private Boolean useHighways;
    private Boolean useTraffic;

    @NotNull
    private RouteOptimizationTypeEnum optimizationType;

    @NotNull
    private NavigationProviderEnum navigationProvider;

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

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
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

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
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
}