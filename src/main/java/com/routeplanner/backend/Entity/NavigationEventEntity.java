package com.routeplanner.backend.Entity;

import com.routeplanner.backend.Enums.NavigationProviderEnum;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "navigation_event")
public class NavigationEventEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_plan_id", nullable = false)
    private RoutePlanEntity routePlan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_stop_id", nullable = false)
    private RouteStopEntity routeStop;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 30)
    private NavigationProviderEnum provider;

    @Column(name = "navigation_url", nullable = false, length = 1000)
    private String navigationUrl;

    @Column(name = "opened_at", nullable = false)
    private Timestamp openedAt;

    public RoutePlanEntity getRoutePlan() {
        return routePlan;
    }

    public void setRoutePlan(RoutePlanEntity routePlan) {
        this.routePlan = routePlan;
    }

    public RouteStopEntity getRouteStop() {
        return routeStop;
    }

    public void setRouteStop(RouteStopEntity routeStop) {
        this.routeStop = routeStop;
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

    public Timestamp getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Timestamp openedAt) {
        this.openedAt = openedAt;
    }
}
