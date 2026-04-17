package com.routeplanner.backend.Controller;

import com.routeplanner.backend.DTO.Request.CreateRouteImportPreviewRequest;
import com.routeplanner.backend.DTO.Response.RouteImportPreviewResponse;
import com.routeplanner.backend.DTO.Response.RoutePlanResponse;
import com.routeplanner.backend.Mapper.RoutePlanMapper;
import com.routeplanner.backend.Service.RouteImportService;
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