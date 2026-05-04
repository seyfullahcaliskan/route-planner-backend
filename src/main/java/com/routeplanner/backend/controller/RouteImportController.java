package com.routeplanner.backend.controller;

import com.routeplanner.backend.dto.request.CreateRouteImportPreviewRequest;
import com.routeplanner.backend.dto.response.RouteImportPreviewResponse;
import com.routeplanner.backend.dto.response.RoutePlanResponse;
import com.routeplanner.backend.mapper.RoutePlanMapper;
import com.routeplanner.backend.service.RouteImportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/route-imports")
public class RouteImportController {

    private final RouteImportService routeImportService;

    public RouteImportController(RouteImportService routeImportService) {
        this.routeImportService = routeImportService;
    }

    @PostMapping("/preview")
    public RouteImportPreviewResponse preview(@Valid @RequestBody CreateRouteImportPreviewRequest request) {
        return routeImportService.preview(request);
    }

    @PostMapping("/confirm")
    public RoutePlanResponse confirm(@Valid @RequestBody CreateRouteImportPreviewRequest request) {
        return RoutePlanMapper.toResponse(routeImportService.confirm(request));
    }
}