package com.routeplanner.backend.Service.Impl;

import com.routeplanner.backend.DTO.Response.NavigationUrlResponse;
import com.routeplanner.backend.Entity.NavigationEventEntity;
import com.routeplanner.backend.Entity.RoutePlanEntity;
import com.routeplanner.backend.Entity.RouteStopEntity;
import com.routeplanner.backend.Enums.NavigationProviderEnum;
import com.routeplanner.backend.Repository.NavigationEventRepository;
import com.routeplanner.backend.Repository.RouteStopRepository;
import com.routeplanner.backend.Service.NavigationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Transactional
public class NavigationServiceImpl implements NavigationService {

    private final RouteStopRepository routeStopRepository;
    private final NavigationEventRepository navigationEventRepository;

    public NavigationServiceImpl(RouteStopRepository routeStopRepository,
                                 NavigationEventRepository navigationEventRepository) {
        this.routeStopRepository = routeStopRepository;
        this.navigationEventRepository = navigationEventRepository;
    }

    @Override
    public NavigationUrlResponse buildNavigationUrl(UUID routeStopId, NavigationProviderEnum providerOverride) {
        RouteStopEntity stop = routeStopRepository.findById(routeStopId)
                .orElseThrow(() -> new RuntimeException("Route stop not found: " + routeStopId));

        RoutePlanEntity plan = stop.getRoutePlan();
        NavigationProviderEnum provider =
                providerOverride != null ? providerOverride : plan.getNavigationProvider();

        String url = buildUrl(provider, stop);

        stop.setNavigationUrl(url);
        stop.setLastNavigationOpenedAt(now());
        routeStopRepository.save(stop);

        NavigationEventEntity event = new NavigationEventEntity();
        event.setRoutePlan(plan);
        event.setRouteStop(stop);
        event.setProvider(provider);
        event.setNavigationUrl(url);
        event.setOpenedAt(now());
        navigationEventRepository.save(event);

        NavigationUrlResponse response = new NavigationUrlResponse();
        response.setRouteStopId(stop.getId());
        response.setProvider(provider);
        response.setNavigationUrl(url);
        return response;
    }

    private String buildUrl(NavigationProviderEnum provider, RouteStopEntity stop) {
        BigDecimal latitude = stop.getLatitude();
        BigDecimal longitude = stop.getLongitude();
        String address = stop.getRawAddress() == null ? "" : stop.getRawAddress();

        return switch (provider) {
            case GOOGLE_MAPS -> buildGoogleMapsUrl(latitude, longitude, address);
            case YANDEX_MAPS -> buildYandexMapsUrl(latitude, longitude, address);
            case APPLE_MAPS -> buildAppleMapsUrl(latitude, longitude, address);
        };
    }

    private String buildGoogleMapsUrl(BigDecimal latitude, BigDecimal longitude, String address) {
        if (latitude != null && longitude != null) {
            return "https://www.google.com/maps/dir/?api=1&destination="
                    + latitude + "," + longitude + "&travelmode=driving";
        }
        return "https://www.google.com/maps/dir/?api=1&destination="
                + URLEncoder.encode(address, StandardCharsets.UTF_8) + "&travelmode=driving";
    }

    private String buildYandexMapsUrl(BigDecimal latitude, BigDecimal longitude, String address) {
        if (latitude != null && longitude != null) {
            return "https://yandex.com/maps/?rtext=~" + latitude + "," + longitude + "&rtt=auto";
        }
        return "https://yandex.com/maps/?text=" + URLEncoder.encode(address, StandardCharsets.UTF_8);
    }

    private String buildAppleMapsUrl(BigDecimal latitude, BigDecimal longitude, String address) {
        if (latitude != null && longitude != null) {
            return "http://maps.apple.com/?daddr=" + latitude + "," + longitude + "&dirflg=d";
        }
        return "http://maps.apple.com/?daddr=" + URLEncoder.encode(address, StandardCharsets.UTF_8);
    }

    private Timestamp now() {
        return Timestamp.valueOf(ZonedDateTime.now(ZoneId.of("Europe/Istanbul")).toLocalDateTime());
    }
}
