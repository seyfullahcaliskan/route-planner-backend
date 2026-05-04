package com.routeplanner.backend.service.impl;

import com.routeplanner.backend.dto.request.CreateUserSavedPlaceRequest;
import com.routeplanner.backend.dto.response.UserSavedPlaceResponse;
import com.routeplanner.backend.entity.UserSavedPlaceEntity;
import com.routeplanner.backend.repository.UserSavedPlaceRepository;
import com.routeplanner.backend.service.UserSavedPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSavedPlaceServiceImpl implements UserSavedPlaceService {

    private final UserSavedPlaceRepository repository;

    @Override
    public UserSavedPlaceResponse create(CreateUserSavedPlaceRequest request) {

        UserSavedPlaceEntity entity = UserSavedPlaceEntity.builder()
                .userId(request.getUserId())
                .placeName(request.getPlaceName())
                .placeType(request.getPlaceType())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDefaultStart(Boolean.TRUE.equals(request.getIsDefaultStart()))
                .isDefaultEnd(Boolean.TRUE.equals(request.getIsDefaultEnd()))
                .build();

        entity = repository.save(entity);

        return map(entity);
    }

    @Override
    public List<UserSavedPlaceResponse> getUserPlaces(UUID userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    private UserSavedPlaceResponse map(UserSavedPlaceEntity e) {
        return UserSavedPlaceResponse.builder()
                .id(e.getId())
                .placeName(e.getPlaceName())
                .placeType(e.getPlaceType())
                .address(e.getAddress())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .isDefaultStart(e.getIsDefaultStart())
                .isDefaultEnd(e.getIsDefaultEnd())
                .build();
    }
}
