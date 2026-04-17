package com.routeplanner.backend.Controller;

import com.routeplanner.backend.DTO.Request.UpdateRouteStopStatusRequest;
import com.routeplanner.backend.DTO.Response.RouteStopResponse;
import com.routeplanner.backend.Mapper.RouteStopMapper;
import com.routeplanner.backend.Service.RouteStopService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/route-stops")
public class RouteStopController {

    private final RouteStopService routeStopService;

    public RouteStopController(RouteStopService routeStopService) {
        this.routeStopService = routeStopService;
    }

    @PostMapping("/{routeStopId}/deliver")
    public RouteStopResponse markDelivered(@PathVariable UUID routeStopId,
                                           @RequestBody(required = false) UpdateRouteStopStatusRequest request) {
        return RouteStopMapper.toResponse(
                routeStopService.markDelivered(routeStopId, request == null ? null : request.getNote())
        );
    }

    @PostMapping("/{routeStopId}/skip")
    public RouteStopResponse markSkipped(@PathVariable UUID routeStopId,
                                         @RequestBody(required = false) UpdateRouteStopStatusRequest request) {
        return RouteStopMapper.toResponse(
                routeStopService.markSkipped(routeStopId, request == null ? null : request.getNote())
        );
    }

    @PostMapping("/{routeStopId}/fail")
    public RouteStopResponse markFailed(@PathVariable UUID routeStopId,
                                        @RequestBody(required = false) UpdateRouteStopStatusRequest request) {
        return RouteStopMapper.toResponse(
                routeStopService.markFailed(routeStopId, request == null ? null : request.getNote())
        );
    }
}