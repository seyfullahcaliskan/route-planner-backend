package com.routeplanner.backend.Service.Impl;

import com.routeplanner.backend.DTO.Request.CreateRouteImportPreviewRequest;
import com.routeplanner.backend.DTO.Request.CreateRoutePlanRequest;
import com.routeplanner.backend.DTO.Request.CreateRouteStopRequest;
import com.routeplanner.backend.DTO.Request.ReoptimizeRouteRequest;
import com.routeplanner.backend.DTO.Response.RouteImportPreviewItemResponse;
import com.routeplanner.backend.DTO.Response.RouteImportPreviewResponse;
import com.routeplanner.backend.Entity.RoutePlanEntity;
import com.routeplanner.backend.Service.RouteImportService;
import com.routeplanner.backend.Service.RouteOptimizationService;
import com.routeplanner.backend.Service.RoutePlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.routeplanner.backend.DTO.Geocoding.GeocodingResult;
import com.routeplanner.backend.Service.GeocodingService;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RouteImportServiceImpl implements RouteImportService {

    private final RoutePlanService routePlanService;
    private final GeocodingService geocodingService;
    private final RouteOptimizationService routeOptimizationService;

    public RouteImportServiceImpl(RoutePlanService routePlanService,
                                  GeocodingService geocodingService, RouteOptimizationService routeOptimizationService) {
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

        if (request.getStops().size() > 25) {
            throw new RuntimeException("Şimdilik tek rotada maksimum 25 adres destekleniyor. 25 üzeri adresler için parçalı optimizasyon eklenecek.");
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

        if (request.getStartAddress() == null || request.getStartAddress().trim().isEmpty()) {
            throw new RuntimeException("Başlangıç adresi boş olamaz.");
        }

        RouteImportPreviewResponse preview = preview(request);

        if (preview.getInvalidCount() > 0) {
            throw new RuntimeException("Geçersiz adresler var. Önce düzeltin.");
        }

        String startAddress = request.getStartAddress().trim();

        String endAddress =
                request.getEndAddress() != null && !request.getEndAddress().trim().isEmpty()
                        ? request.getEndAddress().trim()
                        : startAddress;

        CreateRoutePlanRequest planRequest = new CreateRoutePlanRequest();

        planRequest.setUserId(request.getUserId());
        planRequest.setTitle(request.getTitle());
        planRequest.setDescription(request.getDescription());
        planRequest.setRouteDate(request.getRouteDate());

        planRequest.setStartAddress(startAddress);
        planRequest.setEndAddress(endAddress);

        if (request.getStartLatitude() != null && request.getStartLongitude() != null) {
            planRequest.setStartLatitude(request.getStartLatitude());
            planRequest.setStartLongitude(request.getStartLongitude());
        } else {
            GeocodingResult startGeo = geocodingService.validateAndGeocode(startAddress);

            if (!Boolean.TRUE.equals(startGeo.getSuccess())) {
                throw new RuntimeException(
                        "Başlangıç adresi çözümlenemedi: " + startGeo.getValidationMessage()
                );
            }

            if (startGeo.getLatitude() == null || startGeo.getLongitude() == null) {
                throw new RuntimeException("Başlangıç adresi için koordinat üretilemedi.");
            }

            planRequest.setStartLatitude(startGeo.getLatitude());
            planRequest.setStartLongitude(startGeo.getLongitude());
        }

        if (request.getEndLatitude() != null && request.getEndLongitude() != null) {
            planRequest.setEndLatitude(request.getEndLatitude());
            planRequest.setEndLongitude(request.getEndLongitude());
        } else {
            GeocodingResult endGeo = geocodingService.validateAndGeocode(endAddress);

            if (!Boolean.TRUE.equals(endGeo.getSuccess())) {
                throw new RuntimeException(
                        "Bitiş adresi çözümlenemedi: " + endGeo.getValidationMessage()
                );
            }

            if (endGeo.getLatitude() == null || endGeo.getLongitude() == null) {
                throw new RuntimeException("Bitiş adresi için koordinat üretilemedi.");
            }

            planRequest.setEndLatitude(endGeo.getLatitude());
            planRequest.setEndLongitude(endGeo.getLongitude());
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
