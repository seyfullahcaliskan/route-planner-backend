package com.routeplanner.backend.Service.Impl;

import com.routeplanner.backend.DTO.Request.CreateRouteImportPreviewRequest;
import com.routeplanner.backend.DTO.Request.CreateRoutePlanRequest;
import com.routeplanner.backend.DTO.Request.CreateRouteStopRequest;
import com.routeplanner.backend.DTO.Response.RouteImportPreviewItemResponse;
import com.routeplanner.backend.DTO.Response.RouteImportPreviewResponse;
import com.routeplanner.backend.Entity.RoutePlanEntity;
import com.routeplanner.backend.Service.RouteImportService;
import com.routeplanner.backend.Service.RoutePlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RouteImportServiceImpl implements RouteImportService {

    private final RoutePlanService routePlanService;

    public RouteImportServiceImpl(RoutePlanService routePlanService) {
        this.routePlanService = routePlanService;
    }

    @Override
    @Transactional(readOnly = true)
    public RouteImportPreviewResponse preview(CreateRouteImportPreviewRequest request) {
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

            boolean valid = stop.getRawAddress() != null && !stop.getRawAddress().trim().isEmpty();
            item.setValid(valid);
            item.setValidationMessage(valid ? "Hazır" : "Adres boş olamaz");

            if (valid) {
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
        RouteImportPreviewResponse preview = preview(request);

        if (preview.getInvalidCount() > 0) {
            throw new RuntimeException("Geçersiz adresler var. Önce düzeltin.");
        }

        CreateRoutePlanRequest planRequest = new CreateRoutePlanRequest();
        planRequest.setUserId(request.getUserId());
        planRequest.setTitle(request.getTitle());
        planRequest.setDescription(request.getDescription());
        planRequest.setRouteDate(request.getRouteDate());
        planRequest.setStartAddress(request.getStartAddress());
        planRequest.setStartLatitude(request.getStartLatitude());
        planRequest.setStartLongitude(request.getStartLongitude());
        planRequest.setEndAddress(request.getEndAddress());
        planRequest.setEndLatitude(request.getEndLatitude());
        planRequest.setEndLongitude(request.getEndLongitude());
        planRequest.setUseTolls(request.getUseTolls());
        planRequest.setUseHighways(request.getUseHighways());
        planRequest.setUseTraffic(request.getUseTraffic());
        planRequest.setOptimizationType(request.getOptimizationType());
        planRequest.setNavigationProvider(request.getNavigationProvider());

        RoutePlanEntity routePlan = routePlanService.createRoutePlan(planRequest);
        routePlanService.addStops(routePlan.getId(), request.getStops());

        return routePlan;
    }
}
