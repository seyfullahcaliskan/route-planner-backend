package com.routeplanner.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "user_saved_place")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSavedPlaceEntity extends BaseEntity{

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "place_name", nullable = false)
    private String placeName;

    @Column(name = "place_type", nullable = false)
    private String placeType; // HOME, WORK, WAREHOUSE, CUSTOM

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false)
    private BigDecimal longitude;

    @Column(name = "is_default_start")
    private Boolean isDefaultStart;

    @Column(name = "is_default_end")
    private Boolean isDefaultEnd;
}
