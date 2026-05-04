package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.geocoding.GeocodingResult;

public interface GeocodingService {
    GeocodingResult validateAndGeocode(String rawAddress);
}
