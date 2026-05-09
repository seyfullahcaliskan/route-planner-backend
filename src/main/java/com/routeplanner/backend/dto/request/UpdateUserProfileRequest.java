package com.routeplanner.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Mevcut kullanıcının profilini günceller.
 * Tüm alanlar opsiyonel — null gelen alana dokunulmaz.
 *
 * Email burada bilinçli olarak yok: e-posta değişimi ayrı bir akış olmalı
 * (doğrulama maili gerektirir). Ücretli sürümde eklenebilir.
 */
@Data
public class UpdateUserProfileRequest {

    @Size(min = 1, max = 100)
    private String name;

    @Size(max = 100)
    private String surname;

    @Size(max = 30)
    private String phoneNumber;

    @Size(max = 150)
    private String companyName;

    @Size(max = 500)
    private String avatarUrl;
}
