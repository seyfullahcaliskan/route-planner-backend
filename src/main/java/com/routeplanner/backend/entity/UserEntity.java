package com.routeplanner.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.routeplanner.backend.enums.AuthProviderEnum;
import com.routeplanner.backend.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_provider_provider_id",
                        columnNames = {"auth_provider", "provider_id"}
                )
        }
)
public class UserEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "surname", length = 100)
    private String surname;

    /** Eskiden NOT NULL idi; artık OAuth kullanıcılarında null kalabiliyor.
     *  Local kullanıcılar için kayıt sırasında email'den otomatik üretiliyor. */
    @Column(name = "username", unique = true, length = 100)
    private String username;

    /** OAuth kullanıcılarında null. Local'de BCrypt hash'lenmiş halde tutulur. */
    @JsonIgnore
    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRoleEnum role = UserRoleEnum.COURIER;

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, length = 30)
    private AuthProviderEnum authProvider = AuthProviderEnum.LOCAL;

    /** Google `sub` veya Apple `sub` (subject) — `auth_provider + provider_id` unique. */
    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
}