package com.routeplanner.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopDto {

    /** Adres etiketi — kurye ekranda bunu görür */
    @NotBlank(message = "Durak adı boş olamaz")
    private String label;

    /** Alıcı adı (opsiyonel) */
    private String recipientName;

    /** Kısa not — kat/daire/kod bilgisi */
    private String note;

    @NotNull(message = "Enlem gerekli")
    @DecimalMin(value = "-90.0", message = "Enlem -90'dan küçük olamaz")
    @DecimalMax(value = "90.0",  message = "Enlem 90'dan büyük olamaz")
    private Double latitude;

    @NotNull(message = "Boylam gerekli")
    @DecimalMin(value = "-180.0", message = "Boylam -180'den küçük olamaz")
    @DecimalMax(value = "180.0",  message = "Boylam 180'den büyük olamaz")
    private Double longitude;
}
