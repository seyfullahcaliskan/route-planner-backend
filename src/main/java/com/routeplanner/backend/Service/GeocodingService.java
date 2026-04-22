package com.routeplanner.backend.Service;

import com.routeplanner.backend.DTO.Geocoding.GeocodingResult;

public interface GeocodingService {
    GeocodingResult validateAndGeocode(String rawAddress);
}
