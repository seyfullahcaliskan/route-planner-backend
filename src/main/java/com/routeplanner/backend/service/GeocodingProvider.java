package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.geocoding.GeocodingResult;

public interface GeocodingProvider {
    GeocodingResult geocode(String rawAddress);
}