package com.routeplanner.backend.DTO.Response;

import com.routeplanner.backend.Enums.NavigationProviderEnum;

import java.util.UUID;

public class NavigationUrlResponse {

    private UUID routeStopId;
    private NavigationProviderEnum provider;
    private String navigationUrl;

    public UUID getRouteStopId() {
        return routeStopId;
    }

    public void setRouteStopId(UUID routeStopId) {
        this.routeStopId = routeStopId;
    }

    public NavigationProviderEnum getProvider() {
        return provider;
    }

    public void setProvider(NavigationProviderEnum provider) {
        this.provider = provider;
    }

    public String getNavigationUrl() {
        return navigationUrl;
    }

    public void setNavigationUrl(String navigationUrl) {
        this.navigationUrl = navigationUrl;
    }
}
