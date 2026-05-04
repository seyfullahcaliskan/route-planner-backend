package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.request.CreateRouteImportPreviewRequest;
import com.routeplanner.backend.dto.response.RouteImportPreviewResponse;
import com.routeplanner.backend.entity.RoutePlanEntity;

public interface RouteImportService {
    RouteImportPreviewResponse preview(CreateRouteImportPreviewRequest request);
    RoutePlanEntity confirm(CreateRouteImportPreviewRequest request);
}
