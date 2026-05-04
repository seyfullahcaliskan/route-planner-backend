package com.routeplanner.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class UserSavedPlaceResponse {

    private UUID id;
    private String placeName;
    private String placeType;

    private String address;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private Boolean isDefaultStart;
    private Boolean isDefaultEnd;
}
