package com.routeplanner.backend.service;

/** OAuth ID-token doğrulamasından sonra elde edilen güvenilir kullanıcı bilgisi. */
public class OAuthUserInfo {

    private final String providerId;     // sub
    private final String email;
    private final boolean emailVerified;
    private final String name;
    private final String avatarUrl;

    public OAuthUserInfo(String providerId, String email, boolean emailVerified, String name, String avatarUrl) {
        this.providerId = providerId;
        this.email = email;
        this.emailVerified = emailVerified;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public String getProviderId() { return providerId; }
    public String getEmail() { return email; }
    public boolean isEmailVerified() { return emailVerified; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
}