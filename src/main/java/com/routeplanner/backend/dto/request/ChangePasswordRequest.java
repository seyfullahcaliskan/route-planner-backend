package com.routeplanner.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Kullanıcının kendi şifresini değiştirme isteği.
 * OAuth (Google/Apple) kullanıcıları bu endpoint'i çağıramaz — backend reddeder.
 */
@Data
public class ChangePasswordRequest {

    /** Eski şifre — doğrulanır; yanlışsa 400 PASSWORD_INCORRECT. */
    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 128)
    private String newPassword;
}
