package com.routeplanner.backend.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Bir kayıtlı yerin (saved place) tamamen veya kısmen güncellenmesi için.
 *
 * Tüm alanlar opsiyonel: null gelen alana dokunulmaz.
 */
@Data
public class UpdateUserSavedPlaceRequest {

    @Size(max = 100)
    private String placeName;

    /** HOME | WORK | WAREHOUSE | STORE | CUSTOM */
    @Size(max = 30)
    private String placeType;

    @Size(max = 500)
    private String address;

    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal longitude;

    private Boolean isDefaultStart;
    private Boolean isDefaultEnd;
}
