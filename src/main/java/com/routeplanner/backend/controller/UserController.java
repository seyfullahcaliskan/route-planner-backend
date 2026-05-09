package com.routeplanner.backend.controller;

import com.routeplanner.backend.dto.request.ChangePasswordRequest;
import com.routeplanner.backend.dto.request.CreateUserRequest;
import com.routeplanner.backend.dto.request.UpdateUserProfileRequest;
import com.routeplanner.backend.dto.response.UserResponse;
import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.exception.ApiErrorCode;
import com.routeplanner.backend.exception.ApiException;
import com.routeplanner.backend.mapper.UserMapper;
import com.routeplanner.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ----- Mevcut kullanıcı (yeni endpoint'ler) -----

    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal UserEntity user) {
        return UserMapper.toResponse(userService.getUser(requireUserId(user)));
    }

    @PutMapping("/me")
    public UserResponse updateMe(@AuthenticationPrincipal UserEntity user,
                                 @Valid @RequestBody UpdateUserProfileRequest request) {
        UserEntity updated = userService.updateProfile(requireUserId(user), request);
        return UserMapper.toResponse(updated);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserEntity user,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(requireUserId(user), request);
        return ResponseEntity.noContent().build();
    }

    // ----- Eski/admin uçları -----

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return UserMapper.toResponse(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        return UserMapper.toResponse(userService.getUser(id));
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return userService.listUsers()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
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
