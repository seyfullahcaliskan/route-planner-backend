package com.routeplanner.backend.Service.Impl;

import com.routeplanner.backend.DTO.Request.CreateRoutePlanRequest;
import com.routeplanner.backend.DTO.Request.CreateRouteStopRequest;
import com.routeplanner.backend.DTO.Response.RouteStopResponse;
import com.routeplanner.backend.Entity.RoutePlanEntity;
import com.routeplanner.backend.Entity.RouteStopEntity;
import com.routeplanner.backend.Entity.UserEntity;
import com.routeplanner.backend.Enums.RoutePlanStatusEnum;
import com.routeplanner.backend.Repository.RoutePlanRepository;
import com.routeplanner.backend.Repository.RouteStopRepository;
import com.routeplanner.backend.Repository.UserRepository;
import com.routeplanner.backend.Service.RoutePlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RoutePlanServiceImpl implements RoutePlanService {

    private final RoutePlanRepository routePlanRepository;
    private final RouteStopRepository routeStopRepository;
    private final UserRepository userRepository;

    public RoutePlanServiceImpl(RoutePlanRepository routePlanRepository,
                                RouteStopRepository routeStopRepository,
                                UserRepository userRepository) {
        this.routePlanRepository = routePlanRepository;
        this.routeStopRepository = routeStopRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RoutePlanEntity createRoutePlan(CreateRoutePlanRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        RoutePlanEntity entity = new RoutePlanEntity();
        entity.setUser(user);
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setRouteDate(request.getRouteDate());
        entity.setStartAddress(request.getStartAddress());
        entity.setStartLatitude(request.getStartLatitude());
        entity.setStartLongitude(request.getStartLongitude());
        entity.setEndAddress(request.getEndAddress());
        entity.setEndLatitude(request.getEndLatitude());
        entity.setEndLongitude(request.getEndLongitude());
        entity.setUseTolls(Boolean.TRUE.equals(request.getUseTolls()));
        entity.setUseHighways(request.getUseHighways() == null || request.getUseHighways());
        entity.setUseTraffic(request.getUseTraffic() == null || request.getUseTraffic());
        entity.setOptimizationType(request.getOptimizationType());
        entity.setNavigationProvider(request.getNavigationProvider());
        entity.setPlanStatus(RoutePlanStatusEnum.DRAFT);

        return routePlanRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoutePlanEntity> listUserRoutes(UUID userId) {
        return routePlanRepository.findByUserIdOrderByDateOfRecordedDesc(userId);
    }

    @Override
    public List<RouteStopEntity> addStops(UUID routePlanId, List<CreateRouteStopRequest> requests) {
        RoutePlanEntity routePlan = routePlanRepository.findById(routePlanId)
                .orElseThrow(() -> new RuntimeException("Route plan not found: " + routePlanId));

        int currentSize = routeStopRepository.findByRoutePlanIdOrderBySequenceNoAsc(routePlanId).size();
        List<RouteStopEntity> result = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            CreateRouteStopRequest request = requests.get(i);

            RouteStopEntity stop = new RouteStopEntity();
            stop.setRoutePlan(routePlan);
            stop.setExternalReference(request.getExternalReference());
            stop.setCustomerName(request.getCustomerName());
            stop.setCustomerPhone(request.getCustomerPhone());
            stop.setRawAddress(request.getRawAddress());
            stop.setPriorityNo(request.getPriorityNo() == null ? 0 : request.getPriorityNo());
            stop.setDeliveryNote(request.getDeliveryNote());
            stop.setSequenceNo(currentSize + i + 1);

            result.add(routeStopRepository.save(stop));
        }

        routePlan.setTotalStopCount(currentSize + requests.size());
        routePlanRepository.save(routePlan);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteStopEntity> getStops(UUID routePlanId) {
        return routeStopRepository.findByRoutePlanIdOrderBySequenceNoAsc(routePlanId);
    }
}