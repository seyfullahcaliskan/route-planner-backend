package com.routeplanner.backend.security;

import com.routeplanner.backend.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_PROVIDER = "provider";

    private final JwtProperties props;
    private final SecretKey signingKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public JwtTokenProvider(JwtProperties props) {
        this.props = props;

        byte[] keyBytes = props.getSecret().getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret en az 32 byte (256-bit) olmalı. Şu an: " + keyBytes.length + " byte."
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /** Access token: 15 dakika (varsayılan). */
    public String generateAccessToken(UserEntity user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofMinutes(props.getAccessTokenValidityMinutes()));

        return Jwts.builder()
                .setIssuer(props.getIssuer())
                .setSubject(user.getId().toString())
                .claim(CLAIM_EMAIL, user.getEmail())
                .claim(CLAIM_ROLE, user.getRole() == null ? "COURIER" : user.getRole().name())
                .claim(CLAIM_PROVIDER, user.getAuthProvider() == null ? "LOCAL" : user.getAuthProvider().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Refresh token: kriptografik olarak random 256-bit string.
     * JWT DEĞİL — DB'de hash'i tutulur. Bu sayede revoke edilebilir.
     */
    public String generateRefreshTokenRaw() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Refresh token DB'de ham halde tutulmaz; SHA-256 hash'i tutulur. */
    public String hashRefreshToken(String rawToken) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 hash hesaplanamadı", e);
        }
    }

    public Instant refreshTokenExpiry() {
        return Instant.now().plus(Duration.ofDays(props.getRefreshTokenValidityDays()));
    }

    public Instant accessTokenExpiry() {
        return Instant.now().plus(Duration.ofMinutes(props.getAccessTokenValidityMinutes()));
    }

    /** Returns null on invalid/expired token (so caller can decide). */
    public Claims parseAndValidate(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(signingKey)
                    .requireIssuer(props.getIssuer())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public UUID extractUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }
}