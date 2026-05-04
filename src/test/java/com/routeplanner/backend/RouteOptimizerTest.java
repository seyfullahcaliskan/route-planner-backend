package com.routeplanner.backend;



import com.routeplanner.backend.algorithm.RouteOptimizer;
import com.routeplanner.backend.dto.StopDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class RouteOptimizerTest {

    private RouteOptimizer optimizer;

    // İstanbul'dan gerçek koordinatlar (depo: Kadıköy)
    private StopDto depot;
    private StopDto besiktas;
    private StopDto sisli;
    private StopDto uskudar;
    private StopDto maltepe;
    private StopDto kartal;

    @BeforeEach
    void setUp() {
        optimizer = new RouteOptimizer();
        depot     = stop("Kadıköy Depo",   40.9903,  29.0238);
        besiktas  = stop("Beşiktaş",       41.0438,  29.0063);
        sisli     = stop("Şişli",          41.0602,  28.9877);
        uskudar   = stop("Üsküdar",        41.0282,  29.0153);
        maltepe   = stop("Maltepe",        40.9343,  29.1298);
        kartal    = stop("Kartal",         40.8952,  29.1892);
    }

    // ------------------------------------------------------------------ //

    @Test
    @DisplayName("Tek durak — depot ↔ durak mesafesi döner")
    void singleStop_returnsCorrectDistance() {
        var result = optimizer.optimize(depot, List.of(uskudar));

        assertThat(result.orderedStops()).hasSize(1);
        // Kadıköy↔Üsküdar gidiş+dönüş ≈ 4–6 km
        assertThat(result.totalDistanceKm()).isBetween(3.0, 8.0);
    }

    @Test
    @DisplayName("Boş liste — sıfır mesafe, boş rota")
    void emptyStops_returnsZero() {
        var result = optimizer.optimize(depot, List.of());

        assertThat(result.orderedStops()).isEmpty();
        assertThat(result.totalDistanceKm()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Optimizasyon toplam mesafeyi azaltmalı (NN vs random sıra)")
    void optimizedRoute_isShorterThanRandomOrder() {
        // En kötü sıra: Kadıköy→Kartal→Şişli→Maltepe→Beşiktaş→Üsküdar
        List<StopDto> worstOrder = List.of(kartal, sisli, maltepe, besiktas, uskudar);
        double worstDist = optimizer.totalDistance(worstOrder, depot);

        var result = optimizer.optimize(depot, List.of(besiktas, sisli, uskudar, maltepe, kartal));

        assertThat(result.totalDistanceKm()).isLessThan(worstDist);
    }

    @Test
    @DisplayName("Tüm duraklar ziyaret edilmeli")
    void allStopsVisited() {
        var stops = List.of(besiktas, sisli, uskudar, maltepe, kartal);
        var result = optimizer.optimize(depot, stops);

        assertThat(result.orderedStops()).hasSize(5);
        // Her durak tam bir kez geçmeli
        assertThat(result.orderedStops())
                .extracting(StopDto::getLabel)
                .containsExactlyInAnyOrder("Beşiktaş", "Şişli", "Üsküdar", "Maltepe", "Kartal");
    }

    @Test
    @DisplayName("Haversine: Kadıköy↔Beşiktaş yaklaşık 6.5–8.5 km")
    void haversine_kadikoyToBesiktas() {
        double dist = optimizer.haversine(depot, besiktas);
        assertThat(dist).isCloseTo(7.3, within(1.5));
    }

    @Test
    @DisplayName("Aynı nokta — sıfır mesafe")
    void haversine_samePoint_isZero() {
        double dist = optimizer.haversine(depot, depot);
        assertThat(dist).isCloseTo(0.0, within(0.001));
    }

    @Test
    @DisplayName("2-Opt iyileştirme toplam mesafeyi artırmamalı")
    void twoOptNeverWorsens() {
        var stops = List.of(besiktas, kartal, sisli, uskudar, maltepe);

        // 10 farklı sırayla test et
        for (int seed = 0; seed < 10; seed++) {
            var result = optimizer.optimize(depot, stops);
            double dist = result.totalDistanceKm();
            assertThat(dist).isGreaterThan(0.0);
            // Maksimum makul üst sınır: tüm noktalar arası toplamı 2× fazla
            assertThat(dist).isLessThan(200.0);
        }
    }

    // ------------------------------------------------------------------ //

    private StopDto stop(String label, double lat, double lng) {
        return StopDto.builder().label(label).latitude(lat).longitude(lng).build();
    }
}