package com.routeplanner.backend.service;

import com.routeplanner.backend.dto.request.LoginRequest;
import com.routeplanner.backend.dto.request.OAuthLoginRequest;
import com.routeplanner.backend.dto.request.RefreshTokenRequest;
import com.routeplanner.backend.dto.request.RegisterRequest;
import com.routeplanner.backend.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse loginWithOAuth(OAuthLoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
    void logout(String refreshTokenRaw);
}