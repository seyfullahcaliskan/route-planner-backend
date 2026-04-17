package com.routeplanner.backend.Service;

import com.routeplanner.backend.DTO.Request.CreateRouteImportPreviewRequest;
import com.routeplanner.backend.DTO.Response.RouteImportPreviewResponse;
import com.routeplanner.backend.Entity.RoutePlanEntity;

public interface RouteImportService {
    RouteImportPreviewResponse preview(CreateRouteImportPreviewRequest request);
    RoutePlanEntity confirm(CreateRouteImportPreviewRequest request);
}
