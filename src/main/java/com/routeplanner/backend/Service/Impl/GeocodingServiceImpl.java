package com.routeplanner.backend.Service.Impl;

import com.routeplanner.backend.DTO.Geocoding.GeocodingResult;
import com.routeplanner.backend.Service.GeocodingProvider;
import com.routeplanner.backend.Service.GeocodingService;
import org.springframework.stereotype.Service;

@Service
public class GeocodingServiceImpl implements GeocodingService {

    private final GeocodingProvider geocodingProvider;

    public GeocodingServiceImpl(GeocodingProvider geocodingProvider) {
        this.geocodingProvider = geocodingProvider;
    }

    @Override
    public GeocodingResult validateAndGeocode(String rawAddress) {
        if (rawAddress == null || rawAddress.trim().isEmpty()) {
            GeocodingResult result = new GeocodingResult();
            result.setSuccess(false);
            result.setRawAddress(rawAddress);
            result.setValidationMessage("Adres boş olamaz");
            result.setProvider("SYSTEM");
            return result;
        }

        return geocodingProvider.geocode(rawAddress);
    }
}
