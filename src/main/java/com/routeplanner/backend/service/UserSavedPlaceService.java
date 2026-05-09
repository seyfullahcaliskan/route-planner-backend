package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.request.CreateUserSavedPlaceRequest;
import com.routeplanner.backend.dto.request.UpdateUserSavedPlaceRequest;
import com.routeplanner.backend.dto.response.UserSavedPlaceResponse;

import java.util.List;
import java.util.UUID;

public interface UserSavedPlaceService {

    /** Mevcut kullanıcı için yeni saved place. */
    UserSavedPlaceResponse create(UUID userId, CreateUserSavedPlaceRequest request);

    /** Kullanıcının kayıtlı yerleri (en yeni önce). */
    List<UserSavedPlaceResponse> getUserPlaces(UUID userId);

    /** Tek kayıt — sahibi değilse 403. */
    UserSavedPlaceResponse getOne(UUID userId, UUID placeId);

    /** Kısmi güncelleme — null gelen alana dokunulmaz. */
    UserSavedPlaceResponse update(UUID userId, UUID placeId, UpdateUserSavedPlaceRequest request);

    /** Sil — sahibi değilse 403. */
    void delete(UUID userId, UUID placeId);
}
