package com.routeplanner.backend.dto.response;

import com.routeplanner.backend.enums.AuthProviderEnum;
import com.routeplanner.backend.enums.UserRoleEnum;

import java.util.UUID;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresAt;   // epoch millis
    private long refreshTokenExpiresAt;  // epoch millis
    private String tokenType = "Bearer";
    private UserDto user;

    public static class UserDto {
        public UUID id;
        public String name;
        public String surname;
        public String email;
        public String phoneNumber;
        public UserRoleEnum role;
        public AuthProviderEnum authProvider;
        public String avatarUrl;
        public Boolean emailVerified;

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSurname() { return surname; }
        public void setSurname(String surname) { this.surname = surname; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public UserRoleEnum getRole() { return role; }
        public void setRole(UserRoleEnum role) { this.role = role; }
        public AuthProviderEnum getAuthProvider() { return authProvider; }
        public void setAuthProvider(AuthProviderEnum authProvider) { this.authProvider = authProvider; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public Boolean getEmailVerified() { return emailVerified; }
        public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public long getAccessTokenExpiresAt() { return accessTokenExpiresAt; }
    public void setAccessTokenExpiresAt(long accessTokenExpiresAt) { this.accessTokenExpiresAt = accessTokenExpiresAt; }
    public long getRefreshTokenExpiresAt() { return refreshTokenExpiresAt; }
    public void setRefreshTokenExpiresAt(long refreshTokenExpiresAt) { this.refreshTokenExpiresAt = refreshTokenExpiresAt; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }
}