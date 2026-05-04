package com.routeplanner.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedStopDto {

    /** Sıra numarası (1'den başlar) */
    private int order;

    /** Orijinal durak bilgileri */
    private StopDto stop;

    /** Bu durağa önceki durağa olan mesafe (km) */
    private double distanceFromPreviousKm;

    /** Başlangıçtan bu durağa kümülatif mesafe (km) */
    private double cumulativeDistanceKm;
}

