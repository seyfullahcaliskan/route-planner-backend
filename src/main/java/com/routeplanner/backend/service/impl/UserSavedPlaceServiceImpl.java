package com.routeplanner.backend.service.impl;

import com.routeplanner.backend.dto.request.CreateUserSavedPlaceRequest;
import com.routeplanner.backend.dto.request.UpdateUserSavedPlaceRequest;
import com.routeplanner.backend.dto.response.UserSavedPlaceResponse;
import com.routeplanner.backend.entity.UserSavedPlaceEntity;
import com.routeplanner.backend.exception.ApiErrorCode;
import com.routeplanner.backend.exception.ApiException;
import com.routeplanner.backend.repository.UserSavedPlaceRepository;
import com.routeplanner.backend.service.UserSavedPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSavedPlaceServiceImpl implements UserSavedPlaceService {

    private final UserSavedPlaceRepository repository;

    @Override
    public UserSavedPlaceResponse create(UUID userId, CreateUserSavedPlaceRequest request) {
        // Aynı kullanıcı için tekil "varsayılan başlangıç/bitiş" politikası:
        // Yeni kayıt varsayılan olarak işaretlendiyse, eskileri kaldır.
        if (Boolean.TRUE.equals(request.getIsDefaultStart())) {
            clearOldDefaultStart(userId);
        }
        if (Boolean.TRUE.equals(request.getIsDefaultEnd())) {
            clearOldDefaultEnd(userId);
        }

        UserSavedPlaceEntity entity = UserSavedPlaceEntity.builder()
                .userId(userId)
                .placeName(request.getPlaceName().trim())
                .placeType(request.getPlaceType())
                .address(request.getAddress().trim())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDefaultStart(Boolean.TRUE.equals(request.getIsDefaultStart()))
                .isDefaultEnd(Boolean.TRUE.equals(request.getIsDefaultEnd()))
                .build();

        entity = repository.save(entity);

        return map(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSavedPlaceResponse> getUserPlaces(UUID userId) {
        return repository.findByUserIdOrderByDateOfRecordedDesc(userId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserSavedPlaceResponse getOne(UUID userId, UUID placeId) {
        return map(loadOwned(userId, placeId));
    }

    @Override
    public UserSavedPlaceResponse update(UUID userId, UUID placeId, UpdateUserSavedPlaceRequest request) {
        UserSavedPlaceEntity entity = loadOwned(userId, placeId);

        if (request.getPlaceName() != null && !request.getPlaceName().isBlank()) {
            entity.setPlaceName(request.getPlaceName().trim());
        }
        if (request.getPlaceType() != null && !request.getPlaceType().isBlank()) {
            entity.setPlaceType(request.getPlaceType());
        }
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            entity.setAddress(request.getAddress().trim());
        }
        if (request.getLatitude() != null) {
            entity.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            entity.setLongitude(request.getLongitude());
        }

        if (request.getIsDefaultStart() != null) {
            if (request.getIsDefaultStart()) {
                clearOldDefaultStart(userId);
            }
            entity.setIsDefaultStart(request.getIsDefaultStart());
        }
        if (request.getIsDefaultEnd() != null) {
            if (request.getIsDefaultEnd()) {
                clearOldDefaultEnd(userId);
            }
            entity.setIsDefaultEnd(request.getIsDefaultEnd());
        }

        return map(repository.save(entity));
    }

    @Override
    public void delete(UUID userId, UUID placeId) {
        UserSavedPlaceEntity entity = loadOwned(userId, placeId);
        repository.delete(entity);
    }

    // ----- helpers -----

    private UserSavedPlaceEntity loadOwned(UUID userId, UUID placeId) {
        UserSavedPlaceEntity entity = repository.findById(placeId)
                .orElseThrow(() -> ApiException.notFound(
                        ApiErrorCode.PLACE_NOT_FOUND,
                        "Kayıtlı yer bulunamadı."
                ));
        if (entity.getUserId() == null || !entity.getUserId().equals(userId)) {
            throw ApiException.forbidden(
                    ApiErrorCode.PLACE_FORBIDDEN,
                    "Bu kaydı görüntüleme yetkiniz yok."
            );
        }
        return entity;
    }

    private void clearOldDefaultStart(UUID userId) {
        repository.findByUserIdAndIsDefaultStartTrue(userId)
                .forEach(p -> {
                    p.setIsDefaultStart(false);
                    repository.save(p);
                });
    }

    private void clearOldDefaultEnd(UUID userId) {
        repository.findByUserIdAndIsDefaultEndTrue(userId)
                .forEach(p -> {
                    p.setIsDefaultEnd(false);
                    repository.save(p);
                });
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
