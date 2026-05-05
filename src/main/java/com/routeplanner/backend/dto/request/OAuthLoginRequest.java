package com.routeplanner.backend.dto.request;

import com.routeplanner.backend.enums.AuthProviderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Mobile, Google/Apple SDK'sından aldığı **ID token**'ı bize yollar.
 * Biz Google/Apple public key'leriyle imzayı doğrularız ve email/sub bilgisini güveniriz.
 *
 * Örnek (Google):
 *   { "provider": "GOOGLE", "idToken": "eyJhbGciOi..." }
 */
public class OAuthLoginRequest {

    @NotNull
    private AuthProviderEnum provider;

    @NotBlank
    private String idToken;

    /** Apple bazen sadece ilk login'de email döner; client gerekirse explicit yollar. */
    private String fallbackEmail;
    private String fallbackName;

    private String deviceLabel;

    public AuthProviderEnum getProvider() { return provider; }
    public void setProvider(AuthProviderEnum provider) { this.provider = provider; }
    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
    public String getFallbackEmail() { return fallbackEmail; }
    public void setFallbackEmail(String fallbackEmail) { this.fallbackEmail = fallbackEmail; }
    public String getFallbackName() { return fallbackName; }
    public void setFallbackName(String fallbackName) { this.fallbackName = fallbackName; }
    public String getDeviceLabel() { return deviceLabel; }
    public void setDeviceLabel(String deviceLabel) { this.deviceLabel = deviceLabel; }
}