package com.routeplanner.backend.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * application.yml içinde:
 *
 *  app:
 *    jwt:
 *      secret: "<en az 32 karakter, prod'da env variable'dan oku>"
 *      access-token-validity-minutes: 15
 *      refresh-token-validity-days: 30
 *      issuer: "route-planner"
 *    oauth:
 *      google:
 *        client-ids:
 *          - "<google-cloud-console-ios-client-id>.apps.googleusercontent.com"
 *          - "<google-cloud-console-android-client-id>.apps.googleusercontent.com"
 *          - "<google-cloud-console-web-client-id>.apps.googleusercontent.com"
 *      apple:
 *        client-ids:
 *          - "com.yourcompany.routeplanner"
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret = "change-me-change-me-change-me-change-me-change-me";
    private long accessTokenValidityMinutes = 15;
    private long refreshTokenValidityDays = 30;
    private String issuer = "route-planner";

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public long getAccessTokenValidityMinutes() { return accessTokenValidityMinutes; }
    public void setAccessTokenValidityMinutes(long v) { this.accessTokenValidityMinutes = v; }

    public long getRefreshTokenValidityDays() { return refreshTokenValidityDays; }
    public void setRefreshTokenValidityDays(long v) { this.refreshTokenValidityDays = v; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
}