package com.routeplanner.backend.controller;

import com.routeplanner.backend.dto.request.CreateUserSavedPlaceRequest;
import com.routeplanner.backend.dto.request.UpdateUserSavedPlaceRequest;
import com.routeplanner.backend.dto.response.UserSavedPlaceResponse;
import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.exception.ApiErrorCode;
import com.routeplanner.backend.exception.ApiException;
import com.routeplanner.backend.service.UserSavedPlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Saved places (kullanıcının kayıtlı yerleri) tam CRUD.
 *
 * Tasarım:
 *   - "Mevcut kullanıcı" çağrıları /api/v1/places altında, JWT'den userId okur.
 *   - Geriye dönük uyumluluk için eski path'ler (POST /, GET /{userId}) korundu.
 *   - Tüm yetki kontrolü servis katmanında — başka kullanıcının kaydı 403 döner.
 */
@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class UserSavedPlaceController {

    private final UserSavedPlaceService service;

    // ============================================================
    // YENİ — current user (önerilen)
    // ============================================================

    /** Mevcut kullanıcının kayıtlı yerleri. */
    @GetMapping("/me")
    public List<UserSavedPlaceResponse> myPlaces(@AuthenticationPrincipal UserEntity user) {
        return service.getUserPlaces(requireUserId(user));
    }

    /** Mevcut kullanıcı için yeni kayıt. */
    @PostMapping("/me")
    public UserSavedPlaceResponse createForMe(@AuthenticationPrincipal UserEntity user,
                                              @Valid @RequestBody CreateUserSavedPlaceRequest request) {
        return service.create(requireUserId(user), request);
    }

    /** Tek kayıt detayı — sahibi değilse 403. */
    @GetMapping("/me/{placeId}")
    public UserSavedPlaceResponse getOne(@AuthenticationPrincipal UserEntity user,
                                         @PathVariable UUID placeId) {
        return service.getOne(requireUserId(user), placeId);
    }

    /** Kısmi güncelleme — sahibi değilse 403. */
    @PutMapping("/me/{placeId}")
    public UserSavedPlaceResponse update(@AuthenticationPrincipal UserEntity user,
                                         @PathVariable UUID placeId,
                                         @Valid @RequestBody UpdateUserSavedPlaceRequest request) {
        return service.update(requireUserId(user), placeId, request);
    }

    /** Sil — sahibi değilse 403. */
    @DeleteMapping("/me/{placeId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserEntity user,
                                       @PathVariable UUID placeId) {
        service.delete(requireUserId(user), placeId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // ESKİ — geriye dönük uyumluluk (frontend yavaşça /me'ye geçince silinebilir)
    // ============================================================

    /** Eski endpoint — POST /api/v1/places (body içinde userId vardı). Hâlâ destekliyoruz. */
    @PostMapping
    public UserSavedPlaceResponse createLegacy(@AuthenticationPrincipal UserEntity user,
                                               @Valid @RequestBody CreateUserSavedPlaceRequest request) {
        // userId her zaman JWT'den; istek body'sindeki userId yoksayılır.
        return service.create(requireUserId(user), request);
    }

    /** Eski endpoint — GET /api/v1/places/{userId}. Sadece kendi userId'siyle çağrılabilir. */
    @GetMapping("/{userId}")
    public List<UserSavedPlaceResponse> listLegacy(@AuthenticationPrincipal UserEntity user,
                                                   @PathVariable UUID userId) {
        UUID me = requireUserId(user);
        if (!me.equals(userId)) {
            throw ApiException.forbidden(
                    ApiErrorCode.PLACE_FORBIDDEN,
                    "Başka kullanıcının kayıtlı yerlerini göremezsin."
            );
        }
        return service.getUserPlaces(me);
    }

    // ----- helper -----

    private static UUID requireUserId(UserEntity user) {
        if (user == null || user.getId() == null) {
            throw ApiException.unauthorized(
                    ApiErrorCode.UNAUTHORIZED,
                    "Oturumunuz bulunamadı."
            );
        }
        return user.getId();
    }
}
