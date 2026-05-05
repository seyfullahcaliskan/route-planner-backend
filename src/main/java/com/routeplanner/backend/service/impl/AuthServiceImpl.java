package com.routeplanner.backend.service.impl;

import com.routeplanner.backend.dto.request.LoginRequest;
import com.routeplanner.backend.dto.request.OAuthLoginRequest;
import com.routeplanner.backend.dto.request.RefreshTokenRequest;
import com.routeplanner.backend.dto.request.RegisterRequest;
import com.routeplanner.backend.dto.response.AuthResponse;
import com.routeplanner.backend.entity.RefreshTokenEntity;
import com.routeplanner.backend.entity.UserEntity;
import com.routeplanner.backend.enums.AuthProviderEnum;
import com.routeplanner.backend.enums.UserRoleEnum;
import com.routeplanner.backend.repository.RefreshTokenRepository;
import com.routeplanner.backend.repository.UserRepository;
import com.routeplanner.backend.security.JwtTokenProvider;
import com.routeplanner.backend.service.AuthService;
import com.routeplanner.backend.service.OAuthUserInfo;
import com.routeplanner.backend.service.OAuthVerifierService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthVerifierService oAuthVerifierService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           OAuthVerifierService oAuthVerifierService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.oAuthVerifierService = oAuthVerifierService;
    }

    // ============================================================ //
    // REGISTER
    // ============================================================ //
    @Override
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Bu e-posta zaten kayıtlı.");
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName().trim());
        user.setSurname(request.getSurname() == null ? "" : request.getSurname().trim());
        user.setEmail(email);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(generateUniqueUsername(email));
        user.setRole(UserRoleEnum.COURIER);
        user.setAuthProvider(AuthProviderEnum.LOCAL);
        user.setEmailVerified(false);

        user = userRepository.save(user);

        return issueTokens(user, request.getDeviceLabel());
    }

    // ============================================================ //
    // LOGIN — email + password
    // ============================================================ //
    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("E-posta veya şifre hatalı."));

        if (user.getAuthProvider() != AuthProviderEnum.LOCAL || user.getPassword() == null) {
            throw new IllegalArgumentException(
                    "Bu hesap " + user.getAuthProvider() + " ile oluşturuldu. " +
                            user.getAuthProvider() + " ile giriş yapın."
            );
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("E-posta veya şifre hatalı.");
        }

        return issueTokens(user, request.getDeviceLabel());
    }

    // ============================================================ //
    // OAUTH — Google / Apple
    // ============================================================ //
    @Override
    public AuthResponse loginWithOAuth(OAuthLoginRequest request) {
        AuthProviderEnum provider = request.getProvider();
        if (provider == null || provider == AuthProviderEnum.LOCAL) {
            throw new IllegalArgumentException("Geçersiz OAuth sağlayıcı.");
        }

        OAuthUserInfo info = switch (provider) {
            case GOOGLE -> oAuthVerifierService.verifyGoogleIdToken(request.getIdToken());
            case APPLE -> oAuthVerifierService.verifyAppleIdToken(request.getIdToken());
            default -> throw new IllegalArgumentException("Desteklenmeyen sağlayıcı: " + provider);
        };

        // Apple bazen ilk login dışında email döndürmez — fallback'e izin ver
        String email = info.getEmail();
        if (email == null && request.getFallbackEmail() != null) {
            email = request.getFallbackEmail();
        }
        if (email != null) {
            email = email.trim().toLowerCase();
        }

        // 1) provider+sub eşleşmesi varsa direk login
        Optional<UserEntity> byProvider = userRepository.findByAuthProviderAndProviderId(provider, info.getProviderId());
        if (byProvider.isPresent()) {
            return issueTokens(byProvider.get(), request.getDeviceLabel());
        }

        // 2) Aynı email LOCAL kullanıcı olarak varsa: hesapları bağla (account linking)
        if (email != null) {
            Optional<UserEntity> byEmail = userRepository.findByEmailIgnoreCase(email);
            if (byEmail.isPresent()) {
                UserEntity linked = byEmail.get();
                // Güvenlik: sadece Google/Apple'ın email_verified=true dediği durumda otomatik bağla
                if (info.isEmailVerified() && linked.getAuthProvider() == AuthProviderEnum.LOCAL) {
                    linked.setAuthProvider(provider);
                    linked.setProviderId(info.getProviderId());
                    if (linked.getAvatarUrl() == null && info.getAvatarUrl() != null) {
                        linked.setAvatarUrl(info.getAvatarUrl());
                    }
                    linked.setEmailVerified(true);
                    userRepository.save(linked);
                    return issueTokens(linked, request.getDeviceLabel());
                } else {
                    throw new IllegalArgumentException(
                            "Bu e-posta zaten farklı bir yöntemle kayıtlı. Önceki yöntemle giriş yapın."
                    );
                }
            }
        }

        // 3) Yeni kullanıcı oluştur
        UserEntity user = new UserEntity();
        user.setEmail(email != null ? email : info.getProviderId() + "@" + provider.name().toLowerCase() + ".local");
        user.setEmailVerified(info.isEmailVerified());
        user.setAuthProvider(provider);
        user.setProviderId(info.getProviderId());
        user.setRole(UserRoleEnum.COURIER);
        user.setAvatarUrl(info.getAvatarUrl());

        String displayName = info.getName() != null ? info.getName() : request.getFallbackName();
        if (displayName != null && !displayName.isBlank()) {
            String[] parts = displayName.trim().split("\\s+", 2);
            user.setName(parts[0]);
            user.setSurname(parts.length > 1 ? parts[1] : "");
        } else {
            user.setName(email != null ? email.split("@")[0] : "Kullanıcı");
            user.setSurname("");
        }
        user.setUsername(generateUniqueUsername(user.getEmail()));
        user.setPassword(null); // OAuth user — şifre yok

        user = userRepository.save(user);
        return issueTokens(user, request.getDeviceLabel());
    }

    // ============================================================ //
    // REFRESH (rotation)
    // ============================================================ //
    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = jwtTokenProvider.hashRefreshToken(request.getRefreshToken());

        RefreshTokenEntity entity = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token geçersiz."));

        if (entity.getRevokedAt() != null) {
            // Reuse detected — güvenlik için kullanıcının tüm token'larını iptal et
            refreshTokenRepository.revokeAllForUser(entity.getUser().getId(), nowTs());
            throw new IllegalArgumentException("Refresh token geçersiz (yeniden kullanım algılandı).");
        }
        if (entity.getExpiresAt().toInstant().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token süresi dolmuş.");
        }

        // Eskisini revoke et, yenisini ver (rotation)
        entity.setRevokedAt(nowTs());
        refreshTokenRepository.save(entity);

        return issueTokens(entity.getUser(), entity.getDeviceLabel());
    }

    // ============================================================ //
    // LOGOUT
    // ============================================================ //
    @Override
    public void logout(String refreshTokenRaw) {
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) return;
        String hash = jwtTokenProvider.hashRefreshToken(refreshTokenRaw);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(entity -> {
            if (entity.getRevokedAt() == null) {
                entity.setRevokedAt(nowTs());
                refreshTokenRepository.save(entity);
            }
        });
    }

    // ============================================================ //
    // INTERNALS
    // ============================================================ //
    private AuthResponse issueTokens(UserEntity user, String deviceLabel) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        Instant accessExpiry = jwtTokenProvider.accessTokenExpiry();

        String refreshRaw = jwtTokenProvider.generateRefreshTokenRaw();
        Instant refreshExpiry = jwtTokenProvider.refreshTokenExpiry();

        RefreshTokenEntity refresh = new RefreshTokenEntity();
        refresh.setUser(user);
        refresh.setTokenHash(jwtTokenProvider.hashRefreshToken(refreshRaw));
        refresh.setExpiresAt(Timestamp.from(refreshExpiry));
        refresh.setDeviceLabel(deviceLabel);
        refreshTokenRepository.save(refresh);

        AuthResponse resp = new AuthResponse();
        resp.setAccessToken(accessToken);
        resp.setRefreshToken(refreshRaw);
        resp.setAccessTokenExpiresAt(accessExpiry.toEpochMilli());
        resp.setRefreshTokenExpiresAt(refreshExpiry.toEpochMilli());

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
        resp.setUser(dto);
        return resp;
    }

    private String generateUniqueUsername(String email) {
        String base = email == null ? "user" : email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "_");
        if (base.isEmpty()) base = "user";
        if (base.length() > 80) base = base.substring(0, 80);

        String candidate = base;
        int attempts = 0;
        while (userRepository.existsByUsername(candidate) && attempts < 10) {
            candidate = base + "_" + Integer.toHexString(secureRandom.nextInt(0xFFFFFF));
            attempts++;
        }
        return candidate;
    }

    private Timestamp nowTs() {
        return Timestamp.from(Instant.now());
    }
}