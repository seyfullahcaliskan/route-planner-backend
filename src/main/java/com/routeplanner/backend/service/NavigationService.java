package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.response.NavigationUrlResponse;
import com.routeplanner.backend.enums.NavigationProviderEnum;

import java.util.UUID;

public interface NavigationService {
    NavigationUrlResponse buildNavigationUrl(UUID routeStopId, NavigationProviderEnum providerOverride);
}
