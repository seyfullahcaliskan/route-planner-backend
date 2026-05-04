package com.routeplanner.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeplanner.backend.dto.geocoding.GeocodingResult;
import com.routeplanner.backend.service.GeocodingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Component
public class GoogleGeocodingProvider implements GeocodingProvider {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.maps.api-key}")
    private String apiKey;

    public GoogleGeocodingProvider(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public GeocodingResult geocode(String rawAddress) {
        GeocodingResult result = new GeocodingResult();
        result.setRawAddress(rawAddress);
        result.setProvider("GOOGLE_GEOCODING");

        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://maps.googleapis.com/maps/api/geocode/json")
                    .queryParam("address", rawAddress)
                    .queryParam("key", apiKey)
                    .queryParam("region", "tr")
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            String status = root.path("status").asText();
            if (!"OK".equals(status)) {
                result.setSuccess(false);
                result.setValidationMessage("Geocoding başarısız: " + status);
                return result;
            }

            JsonNode firstResult = root.path("results").get(0);
            if (firstResult == null || firstResult.isMissingNode()) {
                result.setSuccess(false);
                result.setValidationMessage("Adres bulunamadı");
                return result;
            }

            String formattedAddress = firstResult.path("formatted_address").asText();
            JsonNode location = firstResult.path("geometry").path("location");
            boolean partialMatch = firstResult.path("partial_match").asBoolean(false);

            result.setSuccess(true);
            result.setNormalizedAddress(formattedAddress);
            result.setLatitude(BigDecimal.valueOf(location.path("lat").asDouble()));
            result.setLongitude(BigDecimal.valueOf(location.path("lng").asDouble()));
            result.setValidationMessage(
                    partialMatch
                            ? "Adres kısmi eşleşmeyle doğrulandı"
                            : "Adres doğrulandı"
            );

            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setValidationMessage("Geocoding hatası: " + e.getMessage());
            return result;
        }
    }
}