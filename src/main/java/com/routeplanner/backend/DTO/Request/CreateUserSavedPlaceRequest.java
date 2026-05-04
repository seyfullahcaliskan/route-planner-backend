package com.routeplanner.backend.DTO.Request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateUserSavedPlaceRequest {

    private UUID userId;
    private String placeName;
    private String placeType;

    private String address;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private Boolean isDefaultStart;
    private Boolean isDefaultEnd;
}
