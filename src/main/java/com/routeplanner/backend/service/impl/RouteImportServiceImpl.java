package com.routeplanner.backend.service.impl;

import com.routeplanner.backend.dto.geocoding.GeocodingResult;
import com.routeplanner.backend.dto.request.CreateRouteImportPreviewRequest;
import com.routeplanner.backend.dto.request.CreateRoutePlanRequest;
import com.routeplanner.backend.dto.request.CreateRouteStopRequest;
import com.routeplanner.backend.dto.request.ReoptimizeRouteRequest;
import com.routeplanner.backend.dto.response.RouteImportPreviewItemResponse;
import com.routeplanner.backend.dto.response.RouteImportPreviewResponse;
import com.routeplanner.backend.entity.RoutePlanEntity;
import com.routeplanner.backend.service.GeocodingService;
import com.routeplanner.backend.service.RouteImportService;
import com.routeplanner.backend.service.RouteOptimizationService;
import com.routeplanner.backend.service.RoutePlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RouteImportServiceImpl implements RouteImportService {

    private final RoutePlanService routePlanService;
    private final GeocodingService geocodingService;
    private final RouteOptimizationService routeOptimizationService;

    public RouteImportServiceImpl(RoutePlanService routePlanService,
                                  GeocodingService geocodingService,
                                  RouteOptimizationService routeOptimizationService) {
        this.routePlanService = routePlanService;
        this.geocodingService = geocodingService;
        this.routeOptimizationService = routeOptimizationService;
    }

    @Override
    @Transactional(readOnly = true)
    public RouteImportPreviewResponse preview(CreateRouteImportPreviewRequest request) {

        if (request.getStops() == null || request.getStops().isEmpty()) {
            throw new RuntimeException("En az bir adres girilmelidir.");
        }

        if (request.getStops().size() > 100) {
            throw new RuntimeException("Tek rotada maksimum 100 adres destekleniyor.");
        }

        List<RouteImportPreviewItemResponse> items = new ArrayList<>();

        int validCount = 0;
        int invalidCount = 0;

        for (int i = 0; i < request.getStops().size(); i++) {
            CreateRouteStopRequest stop = request.getStops().get(i);

            RouteImportPreviewItemResponse item = new RouteImportPreviewItemResponse();
            item.setRowNo(i + 1);
            item.setCustomerName(stop.getCustomerName());
            item.setCustomerPhone(stop.getCustomerPhone());
            item.setRawAddress(stop.getRawAddress());
            item.setPriorityNo(stop.getPriorityNo());

            if (stop.hasCoordinates()) {
                // Mobile harita üzerinden seçilmiş — geocoding API çağrısı YOK
                item.setValid(true);
                item.setValidationMessage("OK (haritadan seçildi)");
                item.setNormalizedAddress(stop.getRawAddress());
                item.setLatitude(stop.getLatitude());
                item.setLongitude(stop.getLongitude());
                validCount++;
            } else {
                GeocodingResult geocodingResult = geocodingService.validateAndGeocode(stop.getRawAddress());
                item.setValid(Boolean.TRUE.equals(geocodingResult.getSuccess()));
                item.setValidationMessage(geocodingResult.getValidationMessage());
                item.setNormalizedAddress(geocodingResult.getNormalizedAddress());
                item.setLatitude(geocodingResult.getLatitude());
                item.setLongitude(geocodingResult.getLongitude());

                if (Boolean.TRUE.equals(geocodingResult.getSuccess())) {
                    validCount++;
                } else {
                    invalidCount++;
                }
            }

            items.add(item);
        }

        RouteImportPreviewResponse response = new RouteImportPreviewResponse();
        response.setTotalCount(items.size());
        response.setValidCount(validCount);
        response.setInvalidCount(invalidCount);
        response.setItems(items);
        return response;
    }

    @Override
    public RoutePlanEntity confirm(CreateRouteImportPreviewRequest request) {
        if (request == null) {
            throw new RuntimeException("Rota import isteği boş olamaz.");
        }

        // Başlangıç adresi: lat/lng geldiyse adres zorunlu değil
        boolean startCoordsProvided = request.getStartLatitude() != null && request.getStartLongitude() != null;
        if (!startCoordsProvided && (request.getStartAddress() == null || request.getStartAddress().trim().isEmpty())) {
            throw new RuntimeException("Başlangıç adresi veya konumu boş olamaz.");
        }

        RouteImportPreviewResponse preview = preview(request);
        if (preview.getInvalidCount() > 0) {
            throw new RuntimeException("Geçersiz adresler var. Önce düzeltin.");
        }

        String startAddress = request.getStartAddress() == null ? "" : request.getStartAddress().trim();
        String endAddress = request.getEndAddress() != null && !request.getEndAddress().trim().isEmpty()
                ? request.getEndAddress().trim()
                : startAddress;

        CreateRoutePlanRequest planRequest = new CreateRoutePlanRequest();
        planRequest.setUserId(request.getUserId());
        planRequest.setTitle(request.getTitle());
        planRequest.setDescription(request.getDescription());
        planRequest.setRouteDate(request.getRouteDate());
        planRequest.setStartAddress(startAddress);
        planRequest.setEndAddress(endAddress);

        // START coords
        if (startCoordsProvided) {
            planRequest.setStartLatitude(request.getStartLatitude());
            planRequest.setStartLongitude(request.getStartLongitude());
        } else {
            GeocodingResult startGeo = geocodingService.validateAndGeocode(startAddress);
            if (!Boolean.TRUE.equals(startGeo.getSuccess()) || startGeo.getLatitude() == null) {
                throw new RuntimeException("Başlangıç adresi çözümlenemedi: " + startGeo.getValidationMessage());
            }
            planRequest.setStartLatitude(startGeo.getLatitude());
            planRequest.setStartLongitude(startGeo.getLongitude());
        }

        // END coords
        boolean endCoordsProvided = request.getEndLatitude() != null && request.getEndLongitude() != null;
        if (endCoordsProvided) {
            planRequest.setEndLatitude(request.getEndLatitude());
            planRequest.setEndLongitude(request.getEndLongitude());
        } else if (request.getEndAddress() != null && !request.getEndAddress().trim().isEmpty()) {
            GeocodingResult endGeo = geocodingService.validateAndGeocode(endAddress);
            if (!Boolean.TRUE.equals(endGeo.getSuccess()) || endGeo.getLatitude() == null) {
                throw new RuntimeException("Bitiş adresi çözümlenemedi: " + endGeo.getValidationMessage());
            }
            planRequest.setEndLatitude(endGeo.getLatitude());
            planRequest.setEndLongitude(endGeo.getLongitude());
        } else {
            // Bitiş yoksa başlangıçla aynı yap (round-trip)
            planRequest.setEndLatitude(planRequest.getStartLatitude());
            planRequest.setEndLongitude(planRequest.getStartLongitude());
        }

        planRequest.setUseTolls(Boolean.TRUE.equals(request.getUseTolls()));
        planRequest.setUseHighways(Boolean.TRUE.equals(request.getUseHighways()));
        planRequest.setUseTraffic(Boolean.TRUE.equals(request.getUseTraffic()));
        planRequest.setOptimizationType(request.getOptimizationType());
        planRequest.setNavigationProvider(request.getNavigationProvider());

        RoutePlanEntity routePlan = routePlanService.createRoutePlan(planRequest);
        routePlanService.addStops(routePlan.getId(), request.getStops());

        ReoptimizeRouteRequest reoptimizeRequest = new ReoptimizeRouteRequest();
        reoptimizeRequest.setIncludeSkippedStops(true);
        reoptimizeRequest.setIncludeFailedStops(false);
        reoptimizeRequest.setIncludePostponedStops(true);
        reoptimizeRequest.setNote("Rota oluşturma sonrası otomatik optimizasyon");

        routeOptimizationService.reoptimizeRoute(
                routePlan.getId(),
                request.getUserId(),
                reoptimizeRequest
        );

        return routePlan;
    }
}