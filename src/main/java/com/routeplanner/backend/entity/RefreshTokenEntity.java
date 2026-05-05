package com.routeplanner.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * Refresh token rotation:
 *   - Her login'de yeni token üretilir (hash'lenmiş halde DB'de tutulur).
 *   - /auth/refresh çağrısında eski token revoke edilir, yenisi verilir.
 *   - revokedAt != null ⇒ kullanılamaz.
 */
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(
        name = "refresh_token",
        indexes = {
                @Index(name = "idx_refresh_token_hash", columnList = "token_hash"),
                @Index(name = "idx_refresh_token_user", columnList = "user_id")
        }
)
public class RefreshTokenEntity extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /** Token kendisi DB'de tutulmaz; SHA-256 hash'i tutulur. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Timestamp expiresAt;

    @Column(name = "revoked_at")
    private Timestamp revokedAt;

    /** Cihaz tanıma için opsiyonel (örn. "iOS 17 / iPhone 14"). */
    @Column(name = "device_label", length = 200)
    private String deviceLabel;
}