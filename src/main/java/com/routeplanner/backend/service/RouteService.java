package com.routeplanner.backend.service;

import com.routeplanner.backend.algorithm.RouteOptimizer;
import com.routeplanner.backend.dto.OptimizedStopDto;
import com.routeplanner.backend.dto.request.RouteRequest;
import com.routeplanner.backend.dto.response.RouteResponse;
import com.routeplanner.backend.dto.StopDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    /** Şehir içi ortalama hız varsayımı */
    private static final double AVG_SPEED_KMH = 40.0;

    private final RouteOptimizer optimizer;

    /**
     * Verilen request'i optimize eder ve detaylı response döner.
     *
     * Faz 2'de bu metoda kullanıcı planı parametresi eklenecek:
     *   - FREE plan → max 15 durak
     *   - PRO plan  → sınırsız
     */
    public RouteResponse optimize(RouteRequest request) {
        log.info("Rota optimizasyonu başlatıldı. Durak sayısı: {}", request.getStops().size());

        long startMs = System.currentTimeMillis();

        RouteOptimizer.OptimizationResult result = optimizer.optimize(request.getDepot(), request.getStops());

        long elapsed = System.currentTimeMillis() - startMs;
        log.info("Optimizasyon tamamlandı. Toplam mesafe: {:.2f} km, Süre: {} ms",
                result.totalDistanceKm(), elapsed);

        return buildResponse(result, request.getDepot());
    }

    // ------------------------------------------------------------------ //
    //  Response oluşturma
    // ------------------------------------------------------------------ //

    private RouteResponse buildResponse(RouteOptimizer.OptimizationResult result, StopDto depot) {
        List<StopDto> ordered = result.orderedStops();
        List<OptimizedStopDto> enriched = new ArrayList<>(ordered.size());

        double cumulative = 0.0;
        StopDto prev = depot;

        for (int i = 0; i < ordered.size(); i++) {
            StopDto current = ordered.get(i);
            double legDist = optimizer.haversine(prev, current);
            cumulative += legDist;

            enriched.add(OptimizedStopDto.builder()
                    .order(i + 1)
                    .stop(current)
                    .distanceFromPreviousKm(round(legDist))
                    .cumulativeDistanceKm(round(cumulative))
                    .build());

            prev = current;
        }

        double totalKm = result.totalDistanceKm();
        int estimatedMinutes = (int) Math.ceil((totalKm / AVG_SPEED_KMH) * 60);

        return RouteResponse.builder()
                .orderedStops(enriched)
                .totalDistanceKm(round(totalKm))
                .estimatedDurationMinutes(estimatedMinutes)
                .stopCount(ordered.size())
                .algorithm("Nearest-Neighbor + 2-Opt")
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
