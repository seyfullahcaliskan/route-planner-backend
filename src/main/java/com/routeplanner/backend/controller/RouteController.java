package com.routeplanner.backend.controller;


import com.routeplanner.backend.dto.request.RouteRequest;
import com.routeplanner.backend.dto.response.RouteResponse;
import com.routeplanner.backend.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Tag(name = "Rota Optimizasyonu", description = "Kurye rota planlama endpoint'leri")
public class RouteController {

    private final RouteService routeService;

    /**
     * POST /api/v1/routes/optimize
     *
     * Gelen durakları en kısa toplam mesafeye göre sıralar.
     * Yanıtta:
     *   - Sıralı durak listesi (her durak için önceki durağa mesafe + kümülatif mesafe)
     *   - Toplam km
     *   - Tahmini dakika (40 km/h şehir içi hız)
     *
     * Faz 2'de @PreAuthorize ile plan kontrolü eklenecek.
     */
    @PostMapping("/optimize")
    @Operation(
            summary = "Rota optimize et",
            description = "Verilen durakları Nearest Neighbor + 2-Opt algoritmasıyla en kısa rotaya sıralar."
    )
    public ResponseEntity<RouteResponse> optimize(@Valid @RequestBody RouteRequest request) {
        RouteResponse response = routeService.optimize(request);
        return ResponseEntity.ok(response);
    }
}
