package com.routeplanner.backend.service.impl;

import com.routeplanner.backend.dto.geocoding.GeocodingResult;
import com.routeplanner.backend.dto.request.CreateRoutePlanRequest;
import com.routeplanner.backend.dto.request.CreateRouteStopRequest;
import com.routeplanner.backend.entity.RoutePlanEntity;
import com.routeplanner.backend.entity.RouteStopEntity;
import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.enums.RoutePlanStatusEnum;
import com.routeplanner.backend.exception.GeocodingException;
import com.routeplanner.backend.exception.ResourceNotFoundException;
import com.routeplanner.backend.repository.RoutePlanRepository;
import com.routeplanner.backend.repository.RouteStopRepository;
import com.routeplanner.backend.repository.UserRepository;
import com.routeplanner.backend.service.GeocodingService;
import com.routeplanner.backend.service.RoutePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RoutePlanServiceImpl implements RoutePlanService {

    private final RoutePlanRepository routePlanRepository;
    private final RouteStopRepository routeStopRepository;
    private final UserRepository userRepository;
    private final GeocodingService geocodingService;

    @Override
    public RoutePlanEntity createRoutePlan(CreateRoutePlanRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

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
                .orElseThrow(() -> new ResourceNotFoundException("RoutePlan", "id", routePlanId));

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

            GeocodingResult geocodingResult = geocodingService.validateAndGeocode(request.getRawAddress());

            if (!Boolean.TRUE.equals(geocodingResult.getSuccess())) {
                throw new GeocodingException(request.getRawAddress());
            }

            stop.setNormalizedAddress(geocodingResult.getNormalizedAddress());
            stop.setLatitude(geocodingResult.getLatitude());
            stop.setLongitude(geocodingResult.getLongitude());

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