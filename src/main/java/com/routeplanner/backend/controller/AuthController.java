package com.routeplanner.backend.controller;

import com.routeplanner.backend.dto.request.LoginRequest;
import com.routeplanner.backend.dto.request.OAuthLoginRequest;
import com.routeplanner.backend.dto.request.RefreshTokenRequest;
import com.routeplanner.backend.dto.request.RegisterRequest;
import com.routeplanner.backend.dto.response.AuthResponse;
import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/oauth")
    public AuthResponse oauth(@Valid @RequestBody OAuthLoginRequest request) {
        return authService.loginWithOAuth(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    /** Mobil uygulamanın "kullanıcı hâlâ geçerli mi" sorusunu sorabileceği endpoint. */
    @GetMapping("/me")
    public AuthResponse.UserDto me(@AuthenticationPrincipal UserEntity user) {
        AuthResponse.UserDto dto = new AuthResponse.UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setAuthProvider(user.getAuthProvider());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setEmailVerified(user.getEmailVerified());
        return dto;
    }
}