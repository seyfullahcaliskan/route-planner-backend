package com.routeplanner.backend.dto.response;

import com.routeplanner.backend.dto.OptimizedStopDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {

    /** Optimize edilmiş, sıralı durak listesi */
    private List<OptimizedStopDto> orderedStops;

    /** Toplam rota mesafesi (depo → tüm duraklar → depo dönüş dahil), km */
    private double totalDistanceKm;

    /** Tahmini süre (dk) — ortalama 40 km/h şehir içi hız varsayımıyla */
    private int estimatedDurationMinutes;

    /** Durak sayısı */
    private int stopCount;

    /** Kullanılan algoritma bilgisi */
    private String algorithm;
}
