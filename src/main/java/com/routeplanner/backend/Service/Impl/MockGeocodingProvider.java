package com.routeplanner.backend.Service.Impl;

import com.routeplanner.backend.DTO.Geocoding.GeocodingResult;
import com.routeplanner.backend.Service.GeocodingProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MockGeocodingProvider implements GeocodingProvider {

    @Override
    public GeocodingResult geocode(String rawAddress) {
        GeocodingResult result = new GeocodingResult();
        result.setRawAddress(rawAddress);

        if (rawAddress == null || rawAddress.trim().isEmpty()) {
            result.setSuccess(false);
            result.setValidationMessage("Adres boş olamaz");
            result.setProvider("MOCK");
            return result;
        }

        String normalized = rawAddress.trim().replaceAll("\\s+", " ");
        result.setSuccess(true);
        result.setNormalizedAddress(normalized);
        result.setLatitude(BigDecimal.valueOf(41.2061));
        result.setLongitude(BigDecimal.valueOf(32.6204));
        result.setProvider("MOCK");
        result.setValidationMessage("Adres doğrulandı");

        return result;
    }
}
