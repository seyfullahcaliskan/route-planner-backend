package com.routeplanner.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeplanner.backend.exception.OAuthVerificationException;
import com.routeplanner.backend.service.OAuthUserInfo;
import com.routeplanner.backend.service.OAuthVerifierService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Google ID token doğrulaması:
 *   - JWKS: https://www.googleapis.com/oauth2/v3/certs
 *   - issuer: https://accounts.google.com VEYA accounts.google.com
 *   - audience: bizim Google Cloud Console'daki client ID'lerimizden biri
 *
 * Apple ID token doğrulaması:
 *   - JWKS: https://appleid.apple.com/auth/keys
 *   - issuer: https://appleid.apple.com
 *   - audience: app bundle ID (örn. com.yourcompany.routeplanner)
 *
 * JWKS'leri 1 saat memory'de cache'liyoruz; key rotation olunca tekrar çekeriz.
 */
@Slf4j
@Service
public class OAuthVerifierServiceImpl implements OAuthVerifierService {

    private static final String GOOGLE_JWKS_URL = "https://www.googleapis.com/oauth2/v3/certs";
    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    private static final long JWKS_CACHE_TTL_MS = 60L * 60L * 1000L; // 1 saat

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentHashMap<String, PublicKey> googleKeys = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PublicKey> appleKeys = new ConcurrentHashMap<>();
    private final AtomicLong googleKeysFetchedAt = new AtomicLong(0);
    private final AtomicLong appleKeysFetchedAt = new AtomicLong(0);

    @Value("${app.oauth.google.client-ids:}")
    private List<String> googleAllowedAudiences;

    @Value("${app.oauth.apple.client-ids:}")
    private List<String> appleAllowedAudiences;

    @Override
    public OAuthUserInfo verifyGoogleIdToken(String idToken) {
        try {
            Jws<Claims> jws = parseAndVerify(idToken, googleKeys, googleKeysFetchedAt, GOOGLE_JWKS_URL);
            Claims claims = jws.getBody();

            String issuer = claims.getIssuer();
            if (!"https://accounts.google.com".equals(issuer) && !"accounts.google.com".equals(issuer)) {
                throw new OAuthVerificationException("Geçersiz Google issuer: " + issuer);
            }
            validateAudience(claims, googleAllowedAudiences, "Google");

            String sub = claims.getSubject();
            String email = claims.get("email", String.class);
            Boolean emailVerified = claims.get("email_verified", Boolean.class);
            String name = claims.get("name", String.class);
            String picture = claims.get("picture", String.class);

            if (sub == null || email == null) {
                throw new OAuthVerificationException("Google token email/sub içermiyor");
            }

            return new OAuthUserInfo(sub, email, Boolean.TRUE.equals(emailVerified), name, picture);
        } catch (OAuthVerificationException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Google ID token doğrulanamadı: {}", e.getMessage());
            throw new OAuthVerificationException("Google ID token doğrulanamadı");
        }
    }

    @Override
    public OAuthUserInfo verifyAppleIdToken(String idToken) {
        try {
            Jws<Claims> jws = parseAndVerify(idToken, appleKeys, appleKeysFetchedAt, APPLE_JWKS_URL);
            Claims claims = jws.getBody();

            if (!"https://appleid.apple.com".equals(claims.getIssuer())) {
                throw new OAuthVerificationException("Geçersiz Apple issuer: " + claims.getIssuer());
            }
            validateAudience(claims, appleAllowedAudiences, "Apple");

            String sub = claims.getSubject();
            String email = claims.get("email", String.class);
            Boolean emailVerified = parseAppleBool(claims.get("email_verified"));

            if (sub == null) {
                throw new OAuthVerificationException("Apple token sub içermiyor");
            }

            return new OAuthUserInfo(sub, email, Boolean.TRUE.equals(emailVerified), null, null);
        } catch (OAuthVerificationException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Apple ID token doğrulanamadı: {}", e.getMessage());
            throw new OAuthVerificationException("Apple ID token doğrulanamadı");
        }
    }

    // ---------------------------------------------------------------- //

    private Jws<Claims> parseAndVerify(String idToken,
                                       ConcurrentHashMap<String, PublicKey> cache,
                                       AtomicLong fetchedAt,
                                       String jwksUrl) throws Exception {
        // Header'dan kid çek
        String[] parts = idToken.split("\\.");
        if (parts.length != 3) throw new OAuthVerificationException("Geçersiz token formatı");

        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        JsonNode header = objectMapper.readTree(headerJson);
        String kid = header.path("kid").asText(null);
        if (kid == null) throw new OAuthVerificationException("Token header'ında kid yok");

        PublicKey key = cache.get(kid);
        long now = System.currentTimeMillis();
        if (key == null || now - fetchedAt.get() > JWKS_CACHE_TTL_MS) {
            refreshJwks(jwksUrl, cache, fetchedAt);
            key = cache.get(kid);
        }
        if (key == null) {
            throw new OAuthVerificationException("Token'ın kid'i JWKS içinde bulunamadı: " + kid);
        }

        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(idToken);
    }

    private synchronized void refreshJwks(String url,
                                          ConcurrentHashMap<String, PublicKey> cache,
                                          AtomicLong fetchedAt) throws Exception {
        ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new OAuthVerificationException("JWKS alınamadı: " + url);
        }
        JsonNode keys = objectMapper.readTree(resp.getBody()).path("keys");
        cache.clear();

        KeyFactory kf = KeyFactory.getInstance("RSA");
        for (JsonNode jwk : keys) {
            String kty = jwk.path("kty").asText();
            if (!"RSA".equals(kty)) continue;
            String kid = jwk.path("kid").asText();
            String n = jwk.path("n").asText();
            String e = jwk.path("e").asText();
            if (kid.isEmpty() || n.isEmpty() || e.isEmpty()) continue;

            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
            PublicKey pk = kf.generatePublic(new RSAPublicKeySpec(modulus, exponent));
            cache.put(kid, pk);
        }
        fetchedAt.set(System.currentTimeMillis());
    }

    private void validateAudience(Claims claims, List<String> allowed, String providerLabel) {
        if (allowed == null || allowed.isEmpty()) {
            // Yapılandırma yoksa geçer — geliştirme kolaylığı için. Prod'da MUTLAKA yapılandırın.
            log.warn("{} OAuth audience yapılandırması boş — production'da kapatılmalı.", providerLabel);
            return;
        }
        Object aud = claims.get("aud");
        boolean ok;
        if (aud instanceof String s) {
            ok = allowed.contains(s);
        } else if (aud instanceof List<?> list) {
            ok = list.stream().anyMatch(a -> a != null && allowed.contains(a.toString()));
        } else {
            ok = false;
        }
        if (!ok) {
            throw new OAuthVerificationException("Beklenmeyen " + providerLabel + " audience: " + aud);
        }
    }

    private Boolean parseAppleBool(Object value) {
        if (value == null) return null;
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return "true".equalsIgnoreCase(s);
        return null;
    }
}