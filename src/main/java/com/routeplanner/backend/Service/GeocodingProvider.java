package com.routeplanner.backend.Service;

import com.routeplanner.backend.DTO.Geocoding.GeocodingResult;

public interface GeocodingProvider {
    GeocodingResult geocode(String rawAddress);
}