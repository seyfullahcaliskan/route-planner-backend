package com.routeplanner.backend.Service;

import com.routeplanner.backend.DTO.Response.NavigationUrlResponse;
import com.routeplanner.backend.Enums.NavigationProviderEnum;

import java.util.UUID;

public interface NavigationService {
    NavigationUrlResponse buildNavigationUrl(UUID routeStopId, NavigationProviderEnum providerOverride);
}
