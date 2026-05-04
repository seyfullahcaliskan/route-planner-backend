package com.routeplanner.backend.algorithm;

import com.routeplanner.backend.dto.StopDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Rota optimizasyonu — iki aşamalı:
 *   1. Nearest Neighbor  → hızlı başlangıç çözümü (~O(n²))
 *   2. 2-Opt iyileştirme → kenar çaprazlamalarını giderir (~O(n²) iterasyon)
 *
 * 50 durak için toplam süre < 10 ms.
 * Haversine formülü ile gerçek km mesafeleri hesaplanır.
 */
@Component
public class RouteOptimizer {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final int TWO_OPT_MAX_ITERATIONS = 1000;

    // ------------------------------------------------------------------ //
    //  Public API
    // ------------------------------------------------------------------ //

    /**
     * @param depot  Başlangıç / bitiş noktası (depo veya kurye konumu)
     * @param stops  Ziyaret edilecek duraklar (sıra önemi yok)
     * @return Optimize edilmiş sıralı durak listesi (depot dahil değil)
     */
    public OptimizationResult optimize(StopDto depot, List<StopDto> stops) {
        if (stops == null || stops.isEmpty()) {
            return new OptimizationResult(List.of(), 0.0);
        }
        if (stops.size() == 1) {
            double dist = haversine(depot, stops.get(0)) * 2;
            return new OptimizationResult(new ArrayList<>(stops), dist);
        }

        // Aşama 1: Nearest Neighbor
        List<StopDto> route = nearestNeighbor(depot, new ArrayList<>(stops));

        // Aşama 2: 2-Opt iyileştirme
        route = twoOpt(route, depot);

        double totalKm = totalDistance(route, depot);
        return new OptimizationResult(route, totalKm);
    }

    // ------------------------------------------------------------------ //
    //  Nearest Neighbor
    // ------------------------------------------------------------------ //

    private List<StopDto> nearestNeighbor(StopDto depot, List<StopDto> unvisited) {
        List<StopDto> route = new ArrayList<>(unvisited.size());
        StopDto current = depot;

        while (!unvisited.isEmpty()) {
            StopDto nearest = findNearest(current, unvisited);
            route.add(nearest);
            unvisited.remove(nearest);
            current = nearest;
        }
        return route;
    }

    private StopDto findNearest(StopDto from, List<StopDto> candidates) {
        StopDto nearest = null;
        double minDist = Double.MAX_VALUE;
        for (StopDto candidate : candidates) {
            double dist = haversine(from, candidate);
            if (dist < minDist) {
                minDist = dist;
                nearest = candidate;
            }
        }
        return nearest;
    }

    // ------------------------------------------------------------------ //
    //  2-Opt
    // ------------------------------------------------------------------ //

    /**
     * depot → route[0] → route[1] → ... → route[n-1] → depot
     * Çapraz kenarları tespit edip ters çevirerek kısaltır.
     */
    private List<StopDto> twoOpt(List<StopDto> route, StopDto depot) {
        List<StopDto> best = new ArrayList<>(route);
        boolean improved = true;
        int iteration = 0;

        while (improved && iteration < TWO_OPT_MAX_ITERATIONS) {
            improved = false;
            iteration++;

            for (int i = 0; i < best.size() - 1; i++) {
                for (int j = i + 2; j < best.size(); j++) {

                    // Mevcut kenarlar: (i-1 → i) ve (j → j+1)
                    StopDto prevI = (i == 0) ? depot : best.get(i - 1);
                    StopDto nextJ = (j == best.size() - 1) ? depot : best.get(j + 1);

                    double currentDist = haversine(prevI, best.get(i))
                            + haversine(best.get(j), nextJ);

                    double newDist = haversine(prevI, best.get(j))
                            + haversine(best.get(i), nextJ);

                    if (newDist < currentDist - 1e-10) {
                        reverseSegment(best, i, j);
                        improved = true;
                    }
                }
            }
        }
        return best;
    }

    /** [i..j] aralığını in-place ters çevirir */
    private void reverseSegment(List<StopDto> route, int i, int j) {
        while (i < j) {
            StopDto tmp = route.get(i);
            route.set(i, route.get(j));
            route.set(j, tmp);
            i++;
            j--;
        }
    }

    // ------------------------------------------------------------------ //
    //  Haversine mesafe formülü
    // ------------------------------------------------------------------ //

    /**
     * İki koordinat arasındaki kuş uçuşu mesafeyi km cinsinden döndürür.
     */
    public double haversine(StopDto a, StopDto b) {
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(b.getLongitude() - a.getLongitude());

        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(h));
    }

    // ------------------------------------------------------------------ //
    //  Yardımcı: toplam mesafe
    // ------------------------------------------------------------------ //

    public double totalDistance(List<StopDto> route, StopDto depot) {
        if (route.isEmpty()) return 0.0;
        double total = haversine(depot, route.get(0));
        for (int i = 0; i < route.size() - 1; i++) {
            total += haversine(route.get(i), route.get(i + 1));
        }
        total += haversine(route.get(route.size() - 1), depot);
        return total;
    }

    // ------------------------------------------------------------------ //
    //  Result record
    // ------------------------------------------------------------------ //

    public record OptimizationResult(List<StopDto> orderedStops, double totalDistanceKm) {}
}
